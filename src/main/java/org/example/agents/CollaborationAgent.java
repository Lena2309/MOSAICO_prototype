package org.example.agents;

import jakarta.el.LambdaExpression;
import org.example.dto.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class CollaborationAgent extends MosaicoAgent {
    private CollaborationAgent managerAgent;
    private List<CollaborationAgent> collaborationAgentPool;
    private List<MosaicoAgent> agentPool = new ArrayList<>();

    public CollaborationAgent() {
        this(null, null);
        this.agentPool.add(new ReferenceAgent());
    }

    public CollaborationAgent(CollaborationAgent managerAgent) {
        this(managerAgent, null);
    }

    public CollaborationAgent(CollaborationAgent managerAgent, List<MosaicoAgent> agentPool) {
        super(null, null, null, null);
        this.managerAgent = managerAgent;
        if (agentPool != null) {
            this.agentPool.addAll(agentPool);
        }
    }

    // ----------------------------------------------------------

    // REPO ? ou AgentPool
    // TODO: implement repo talk
    public static MosaicoAgent findBestAgentForTask(List<MosaicoAgent> agentPool, List<String> taskKeywords, String taskDescription, MosaicoAgent agentToAvoid) {
        if (agentToAvoid != null) {
            return agentToAvoid;
        }
        return null;
    }

    public CollaborationAgent getManagerAgent() {
        return managerAgent;
    }

    // --------------------------

    public List<MosaicoAgent> getAgentPool() {
        return agentPool;
    }

    public String run(TaskExecutionPlan executionPlan) {
        var taskOutputs = runOrchestrator(executionPlan.getTasks(), executionPlan.getTaskExecutionPlans(), executionPlan.getWorkflowType(), new ArrayList<>(), executionPlan.getEndLoopCondition());
        return taskOutputs.toString();
    }

    public List<TaskOutput> runOrchestrator(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, WorkflowType workflowType, List<TaskOutput> taskOutputs, Optional<LambdaExpression> endLoopCondition) {
        switch (workflowType) {
            case SEQUENTIAL -> taskOutputs = executeSequential(tasks, taskExecutionPlans, taskOutputs);
            case PARALLEL -> taskOutputs = executeParallel(tasks, taskExecutionPlans, taskOutputs);
            case LOOP -> taskOutputs = executeLoop(tasks, taskExecutionPlans, taskOutputs, endLoopCondition);
        }
        return taskOutputs;
    }

    private void executeItem(OrderedMOSAICOExecution item, List<TaskOutput> taskOutputs) {
        if (item instanceof Task task) {
            var necessaryOutputs = taskOutputs.stream()
                    .filter(taskOutput -> task.getOutputDependencies().contains(taskOutput.task()))
                    .toList();
            taskOutputs.add(task.execute(necessaryOutputs));
        } else if (item instanceof TaskExecutionPlan subPlan) {
            runOrchestrator(subPlan.getTasks(), subPlan.getTaskExecutionPlans(), subPlan.getWorkflowType(), taskOutputs, subPlan.getEndLoopCondition());
        }
    }

    private List<TaskOutput> executeSequential(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs) {
        System.out.println("--- Starting Sequential Plan Execution ---");

        var executionQueue = Stream.concat(tasks.stream(), taskExecutionPlans.stream())
                .sorted(Comparator.comparingInt(OrderedMOSAICOExecution::getExecutionOrder))
                .toList();


        for (OrderedMOSAICOExecution executable : executionQueue) {
            executeItem(executable, taskOutputs);
        }

        System.out.println("--- Finished Sequential Plan Order ---");
        return taskOutputs;
    }

    public List<TaskOutput> executeParallel(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs) {
        // TODO: fix parallel execution
        System.out.println("--- Starting Parallel Plan Execution ---");
        var executor = Executors.newCachedThreadPool();

        var allItems = Stream.concat(
                tasks.stream(),
                taskExecutionPlans.stream()
        ).toList();

        if (allItems.isEmpty()) {
            return taskOutputs;
        }

        var threadSafeResults = Collections.synchronizedList(taskOutputs);

        var futures = allItems.stream()
                .map(item -> CompletableFuture.runAsync(() -> {
                    executeItem(item, threadSafeResults);
                }, executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        System.out.println("--- Finished Parallel Plan Order ---");
        return threadSafeResults;
    }


    private List<TaskOutput> executeLoop(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs, Optional<LambdaExpression> endLoopCondition) {
        // TODO: implement loop execution
        System.out.println("--- Starting Loop Plan Execution ---");
        //if (endLoopCondition.isPresent()) {
        //while (endLoopCondition.get() != true) {
        var executionQueue = Stream.concat(tasks.stream(), taskExecutionPlans.stream())
                .sorted(Comparator.comparingInt(OrderedMOSAICOExecution::getExecutionOrder))
                .toList();

        for (OrderedMOSAICOExecution executable : executionQueue) {
            executeItem(executable, taskOutputs);
        }
        //}
        //}

        System.out.println("--- Finished Loop Plan Order ---");
        return taskOutputs;
    }
}
