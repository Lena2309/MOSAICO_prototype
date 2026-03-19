package org.example.parser.util;

import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.example.agents.MosaicoAgent;
import org.example.dto.*;
import org.omg.sysml.lang.sysml.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.example.parser.util.ActionMapper.mapActionToTask;
import static org.example.parser.util.UtilAttributeMapper.getSafeName;

/**
 * Mapper utility for transforming SysML Flow and Control nodes into a hierarchical {@link TaskExecutionPlan}.
 * <p>
 * This mapper interprets SysML succession relationships, handling sequential actions,
 * parallel forks, and iterative loops.
 */
public interface FlowMapper {

    /**
     * Entry point for parsing a set of SysML flows into a root execution plan.
     *
     * @param rootFlows     The top-level succession usages to parse.
     * @param mosaicoAgents Available agents for task assignment.
     * @return A root-level {@link TaskExecutionPlan} containing the flattened or nested task structure.
     */
    static TaskExecutionPlan parseTaskExecutionPlan(List<SuccessionAsUsage> rootFlows, List<MosaicoAgent> mosaicoAgents) {
        var executionOrder = new AtomicInteger(1);
        var rootTasks = new ArrayList<Task>();
        var rootExecutionPlans = new ArrayList<TaskExecutionPlan>();
        var taskOutputParameters = new HashMap<String, Task>();

        processSubNodes(taskOutputParameters, mosaicoAgents, rootFlows, rootTasks, rootExecutionPlans, executionOrder);

        return new TaskExecutionPlan(0, rootTasks, rootExecutionPlans, WorkflowType.SEQUENTIAL, null);
    }

    /**
     * Evaluates a specific SysML element within a succession flow and routes it
     * to the appropriate handler (Action, Fork, Loop, etc.).
     */
    private static void processActionInSuccession(
            Element e,
            List<SuccessionAsUsage> scopeFlows,
            Map<String, Task> taskOutputParameters,
            List<MosaicoAgent> mosaicoAgents,
            List<Task> currentTasks,
            List<TaskExecutionPlan> currentSubPlans,
            AtomicInteger executionOrder,
            Set<Element> processedNodes) {

        // Prevent infinite loops in cyclic graphs
        if (!processedNodes.add(e)) {
            return;
        }

        // Filter out purely structural control nodes
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
                    null
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
                // Resolve data dependencies based on named output parameters
                var dependencies = actionUsage.getInput().stream()
                        .map(Element::getDeclaredName)
                        .map(taskOutputParameters::get)
                        .filter(Objects::nonNull)
                        .toList();

                var newTask = mapActionToTask(actionUsage, mosaicoAgents, dependencies, executionOrder.getAndIncrement());
                currentTasks.add(newTask);

                // Register outputs so subsequent tasks can define them as dependencies
                actionUsage.getOutput().stream()
                        .map(Element::getDeclaredName)
                        .forEach(output -> taskOutputParameters.put(output, newTask));
            } else {
                // If action has internal flows, treat it as a nested sequential sub-plan
                TaskExecutionPlan subPlan = buildSubPlan(e, taskOutputParameters, mosaicoAgents, WorkflowType.SEQUENTIAL, executionOrder);
                currentSubPlans.add(subPlan);
            }
        }
    }

    /**
     * Builds a nested execution plan for composite elements (Loops or Nested Actions).
     */
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

        String executionPlanName = "unnamed";
        LoopCondition condition = null;
        if (e instanceof WhileLoopActionUsage loopActionUsage) {
            for (var child : loopActionUsage.getOwnedRelationship()) {
                for (var subChild : child.getOwnedRelatedElement()) {
                    if (subChild instanceof ActionUsage actionUsage) {
                        executionPlanName = actionUsage.getDeclaredName();
                        break;
                    }
                }
            }
            // TODO julien: how the end condition is parsed and stored as a string as "key word + OCL expression"
            if (loopActionUsage.getUntilArgument() != null) {
                condition = new LoopCondition(LoopKind.LoopUntil, ExpressionBuilder.transpile(loopActionUsage.getUntilArgument()));
            } else if (loopActionUsage.getWhileArgument() != null) {
                condition = new LoopCondition(LoopKind.While, ExpressionBuilder.transpile(loopActionUsage.getUntilArgument()));
            }
        }

        return new TaskExecutionPlan(executionOrder.getAndIncrement(), executionPlanName, subTasks, subPlans, workflowType, condition);
    }

    /**
     * Resolves the text representation of a SysML constraint/expression.
     */
    private static String parseConditionText(Element e) {
        if (e == null) return "";

        // Directly finds
        try {
            var node = NodeModelUtils.getNode(e);
            if (node != null) {
                String rawText = node.getText();
                if (rawText != null && !rawText.isBlank()) {
                    return rawText.trim();
                }
            }
        } catch (Exception ignored) {
            // Fallback to AST parsing if Xtext nodes are stripped or unavailable
        }
        return "couldn't parse condition";
    }

    /**
     * Iterates through a list of flows and processes both source and target nodes.
     */
    static void processSubNodes(
            Map<String, Task> taskOutputParameters,
            List<MosaicoAgent> mosaicoAgents,
            List<SuccessionAsUsage> internalFlows,
            ArrayList<Task> subTasks,
            ArrayList<TaskExecutionPlan> subPlans,
            AtomicInteger subPlanExecutionOrder) {

        var subProcessedNodes = new HashSet<Element>();

        for (var flow : internalFlows) {
            var source = flow.getSource().getFirst();
            var target = flow.getTarget().getFirst();

            processActionInSuccession(source, internalFlows, taskOutputParameters, mosaicoAgents, subTasks, subPlans, subPlanExecutionOrder, subProcessedNodes);
            processActionInSuccession(target, internalFlows, taskOutputParameters, mosaicoAgents, subTasks, subPlans, subPlanExecutionOrder, subProcessedNodes);
        }
    }

    // -----------------------------------------------------------------------------
    //                            INTERNAL UTILITIES
    // -----------------------------------------------------------------------------

    /**
     * Extracts internal SuccessionAsUsage relationships from a SysML element.
     */
    private static List<SuccessionAsUsage> getInternalFlows(Element e) {
        List<SuccessionAsUsage> internalFlows = new ArrayList<>();
        if (e == null) return internalFlows;

        for (var rel : e.getOwnedRelationship()) {
            if (rel instanceof FeatureMembership fm) {
                for (var subChild : fm.getRelatedElement()) {
                    if (subChild == e) continue;

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

    /**
     * Recursively traverses a branch starting from a Fork node until a Join node is encountered.
     */
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

    // -----------------------------------------------------------------------------
    //                            NODE TYPE CHECKS
    // -----------------------------------------------------------------------------

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