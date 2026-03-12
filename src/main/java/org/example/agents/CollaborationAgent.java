package org.example.agents;

import org.example.dto.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

    public CollaborationAgent getManagerAgent() {
        return managerAgent;
    }

    // --------------------------

    public List<MosaicoAgent> getAgentPool() {
        return agentPool;
    }

    public String run(TaskExecutionPlan executionPlan) {
        var taskOutputs = runOrchestrator(executionPlan, executionPlan.getWorkflowType(), new ArrayList<>(), executionPlan.getEndLoopCondition());
        return taskOutputs.toString();
    }

    public List<TaskOutput> runOrchestrator(TaskExecutionPlan taskExecutionPlan, WorkflowType workflowType, List<TaskOutput> taskOutputs, String endLoopCondition) {
        switch (workflowType) {
            case SEQUENTIAL -> taskOutputs = executeSequential(taskExecutionPlan, taskOutputs);
            case PARALLEL -> taskOutputs = executeParallel(taskExecutionPlan, taskOutputs);
            case LOOP -> taskOutputs = executeLoop(taskExecutionPlan, taskOutputs, endLoopCondition);
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
            runOrchestrator(subPlan, subPlan.getWorkflowType(), taskOutputs, subPlan.getEndLoopCondition());
        }
    }

    private List<TaskOutput> executeSequential(TaskExecutionPlan taskExecutionPlan, List<TaskOutput> taskOutputs) {
        System.out.println("--- Starting Sequential Plan Execution ---");

        var executionQueue = Stream.concat(taskExecutionPlan.getTasks().stream(), taskExecutionPlan.getTaskExecutionPlans().stream())
                .sorted(Comparator.comparingInt(OrderedMOSAICOExecution::getExecutionOrder))
                .toList();


        for (OrderedMOSAICOExecution executable : executionQueue) {
            executeItem(executable, taskOutputs);
        }

        System.out.println("--- Finished Sequential Plan Order ---");
        return taskOutputs;
    }

    /**
     * Executes a collection of tasks and sub-plans concurrently.
     * <p>
     * This method utilizes Virtual Threads to handle potentially I/O-bound agent tasks
     * without blocking platform threads. It blocks the calling thread until all
     * parallel items have completed execution.
     */
    private List<TaskOutput> executeParallel(TaskExecutionPlan taskExecutionPlan, List<TaskOutput> taskOutputs) {
        System.out.println("--- Starting Parallel Plan Execution ---");

        var allItems = Stream.concat(taskExecutionPlan.getTasks().stream(), taskExecutionPlan.getTaskExecutionPlans().stream()).toList();
        if (allItems.isEmpty()) {
            return taskOutputs;
        }

        // Ensure the output list can handle concurrent additions from multiple worker threads
        var threadSafeResults = Collections.synchronizedList(taskOutputs);

        // Try-with-resources on the executor ensures proper shutdown and
        // waits for all virtual threads to terminate before exiting the block.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            List<CompletableFuture<Void>> futures = allItems.stream()
                    .map(item -> CompletableFuture.runAsync(() -> {
                                System.out.println("  [Parallel] Starting: " + getIdentifier(item));
                                executeItem(item, threadSafeResults);
                                System.out.println("  [Parallel] Completed: " + getIdentifier(item));
                            }, executor)
                            .exceptionally(ex -> {
                                // Capture and log exceptions within threads to prevent silent failures
                                System.err.println("  [Parallel] FAILED: " + getIdentifier(item) + " -> " + ex.getMessage());
                                ex.printStackTrace();
                                return null;
                            }))
                    .toList();

            // Block the main thread until every submitted task reports completion
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        System.out.println("--- Finished Parallel Plan Order ---");
        return taskOutputs;
    }

    /**
     * Internal helper for logging purposes.
     */
    private String getIdentifier(Object item) {
        if (item instanceof Task t) {
            return "Task " + t.getTaskName();
        }
        if (item instanceof TaskExecutionPlan) {
            return "SubPlan";
        }
        return "Unknown Item";
    }


    private List<TaskOutput> executeLoop(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs, Optional<LambdaExpression> endLoopCondition) {
        // TODO: implement loop execution
        System.out.println("--- Starting Loop Plan Execution ---");

        if (!endLoopCondition.isBlank()) {

            var executionQueue = Stream.concat(taskExecutionPlan.getTasks().stream(), taskExecutionPlan.getTaskExecutionPlans().stream())
                    .sorted(Comparator.comparingInt(OrderedMOSAICOExecution::getExecutionOrder))
                    .toList();

            var repeat = true;
            while (repeat) {
                for (OrderedMOSAICOExecution executable : executionQueue) {
                    executeItem(executable, taskOutputs);
                }


                repeat = false;
            }

        }

        System.out.println("--- Finished Loop Plan Order ---");
        return taskOutputs;
    }
}
