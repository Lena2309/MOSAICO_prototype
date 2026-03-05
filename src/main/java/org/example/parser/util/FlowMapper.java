package org.example.parser.util;

import jakarta.el.LambdaExpression;
import org.example.agents.MosaicoAgent;
import org.example.dto.Task;
import org.example.dto.TaskExecutionPlan;
import org.example.dto.WorkflowType;
import org.omg.sysml.lang.sysml.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.example.parser.util.ActionMapper.mapActionToTask;
import static org.example.parser.util.UtilAttributeMapper.getSafeName;

public interface FlowMapper {
    static TaskExecutionPlan parseTaskExecutionPlan(List<SuccessionAsUsage> rootFlows, List<MosaicoAgent> mosaicoAgents) {
        var executionOrder = new AtomicInteger(1);

        var rootTasks = new ArrayList<Task>();
        var rootExecutionPlans = new ArrayList<TaskExecutionPlan>();
        var taskOutputParameters = new HashMap<String, Task>();
        processSubNodes(taskOutputParameters, mosaicoAgents, rootFlows, rootTasks, rootExecutionPlans, executionOrder);

        return new TaskExecutionPlan(0, rootTasks, rootExecutionPlans, WorkflowType.SEQUENTIAL, Optional.empty());
    }

    private static void processActionInSuccession(
            Element e,
            List<SuccessionAsUsage> scopeFlows,
            Map<String, Task> taskOutputParameters,
            List<MosaicoAgent> mosaicoAgents,
            List<Task> currentTasks,
            List<TaskExecutionPlan> currentSubPlans,
            AtomicInteger executionOrder,
            Set<Element> processedNodes) {

        if (!processedNodes.add(e)) {
            return;
        }

        if (isStartNode(e) || isDoneNode(e) || isJoinNode(e)) {
            return;
        }

        // --- PARALLEL BLOCK HANDLING ---
        if (isForkNode(e)) {
            var parallelPlan = new TaskExecutionPlan(
                    executionOrder.getAndIncrement(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    WorkflowType.PARALLEL,
                    Optional.empty()
            );
            currentSubPlans.add(parallelPlan);

            var outgoingFlows = scopeFlows.stream()
                    .filter(f -> f.getSource().getFirst() == e)
                    .toList();

            var parallelExecutionOrder = new AtomicInteger(1);

            for (var outFlow : outgoingFlows) {
                var branchStart = outFlow.getTarget().getFirst();

                processBranch(
                        branchStart,
                        scopeFlows,
                        taskOutputParameters,
                        mosaicoAgents,
                        parallelPlan.getTasks(),
                        parallelPlan.getTaskExecutionPlans(),
                        parallelExecutionOrder,
                        processedNodes
                );
            }
            return;
        }

        // --- LOOP HANDLING ---
        if (isLoopNode(e)) {
            TaskExecutionPlan loopPlan = buildSubPlan(e, taskOutputParameters, mosaicoAgents, WorkflowType.LOOP, executionOrder);
            currentSubPlans.add(loopPlan);
            return;
        }

        // --- ACTION HANDLING ---
        if (e instanceof ActionUsage actionUsage) {
            if (!hasSubPlans(actionUsage)) {
                var dependencies = actionUsage.getInput().stream()
                        .map(Element::getDeclaredName)
                        .map(taskOutputParameters::get)
                        .filter(Objects::nonNull)
                        .toList();

                var newTask = mapActionToTask(actionUsage, mosaicoAgents, dependencies, executionOrder.getAndIncrement());
                currentTasks.add(newTask);

                actionUsage.getOutput().stream()
                        .map(Element::getDeclaredName)
                        .forEach(output -> taskOutputParameters.put(output, newTask));
            } else {
                TaskExecutionPlan subPlan = buildSubPlan(e, taskOutputParameters, mosaicoAgents, WorkflowType.SEQUENTIAL, executionOrder);
                currentSubPlans.add(subPlan);
            }
        }
    }

    private static TaskExecutionPlan buildSubPlan(
            Element e,
            Map<String, Task> taskOutputParameters,
            List<MosaicoAgent> mosaicoAgents,
            WorkflowType workflowType,
            AtomicInteger executionOrder) {

        List<SuccessionAsUsage> internalFlows = getInternalFlows(e);

        var subTasks = new ArrayList<Task>();
        var subPlans = new ArrayList<TaskExecutionPlan>();
        var subPlanExecutionOrder = new AtomicInteger(1);

        processSubNodes(taskOutputParameters, mosaicoAgents, internalFlows, subTasks, subPlans, subPlanExecutionOrder);

        Optional<LambdaExpression> condition = Optional.empty();
        if (e instanceof WhileLoopActionUsage) {
            // TODO: map the setup/test parts of the while loop
        }

        return new TaskExecutionPlan(executionOrder.getAndIncrement(), subTasks, subPlans, workflowType, condition);
    }

    static void processSubNodes(Map<String, Task> taskOutputParameters, List<MosaicoAgent> mosaicoAgents, List<SuccessionAsUsage> internalFlows, ArrayList<Task> subTasks, ArrayList<TaskExecutionPlan> subPlans, AtomicInteger subPlanExecutionOrder) {
        var subProcessedNodes = new HashSet<Element>();

        for (var flow : internalFlows) {
            var source = flow.getSource().getFirst();
            var target = flow.getTarget().getFirst();

            processActionInSuccession(source, internalFlows, taskOutputParameters, mosaicoAgents, subTasks, subPlans, subPlanExecutionOrder, subProcessedNodes);
            processActionInSuccession(target, internalFlows, taskOutputParameters, mosaicoAgents, subTasks, subPlans, subPlanExecutionOrder, subProcessedNodes);
        }
    }

    // -----------------------------------------------------------------------------
    //                            HELPER METHODS
    // -----------------------------------------------------------------------------

    private static List<SuccessionAsUsage> getInternalFlows(Element e) {
        List<SuccessionAsUsage> internalFlows = new ArrayList<>();
        if (e == null) return internalFlows;

        for (var rel : e.getOwnedRelationship()) {
            if (rel instanceof FeatureMembership fm) {
                for (var subChild : fm.getRelatedElement()) {
                    if (subChild == e) {
                        continue;
                    }

                    if (subChild instanceof SuccessionAsUsage flow) {
                        internalFlows.add(flow);
                    } else if (isLoopNode(e) && subChild instanceof ActionUsage innerAction) {
                        if (!isLoopNode(innerAction)) {
                            internalFlows.addAll(getInternalFlows(innerAction));
                        }
                    }
                }
            }
        }
        return internalFlows;
    }

    private static void processBranch(
            Element current,
            List<SuccessionAsUsage> scopeFlows,
            Map<String, Task> taskOutputParameters,
            List<MosaicoAgent> mosaicoAgents,
            List<Task> branchTasks,
            List<TaskExecutionPlan> branchPlans,
            AtomicInteger branchExecutionOrder,
            Set<Element> processedNodes) {

        if (isJoinNode(current)) {
            return;
        }

        processActionInSuccession(current, scopeFlows, taskOutputParameters, mosaicoAgents, branchTasks, branchPlans, branchExecutionOrder, processedNodes);

        var nextElements = scopeFlows.stream()
                .filter(f -> f.getSource().getFirst() == current || f.getSource().getFirst().equals(current))
                .map(f -> f.getTarget().getFirst())
                .toList();

        for (var next : nextElements) {
            processBranch(next, scopeFlows, taskOutputParameters, mosaicoAgents, branchTasks, branchPlans, branchExecutionOrder, processedNodes);
        }
    }

    private static boolean isStartNode(Element e) {
        return "start".equals(getSafeName(e)) || "InitialNode".equals(e.eClass().getName());
    }

    private static boolean isDoneNode(Element e) {
        return "done".equals(getSafeName(e)) || "ActivityFinalNode".equals(e.eClass().getName());
    }

    private static boolean isForkNode(Element e) {
        return e instanceof ForkNode || "forkNode".equals(getSafeName(e));
    }

    private static boolean isJoinNode(Element e) {
        return e instanceof JoinNode || "joinNode".equals(getSafeName(e));
    }

    private static boolean isLoopNode(Element e) {
        return e instanceof LoopActionUsage || e instanceof WhileLoopActionUsage || "loopAction".equals(getSafeName(e));
    }

    private static boolean hasSubPlans(Element e) {
        return !getInternalFlows(e).isEmpty();
    }
}
