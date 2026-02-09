package org.example.orchestrator;

import jakarta.el.LambdaExpression;
import org.example.agents.CollaborationAgent;
import org.example.dto.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public record ParallelOrchestration(CollaborationAgent collaborationAgent) implements Orchestrator {
    @Override
    public WorkflowType getWorkflowType() {
        return WorkflowType.PARALLEL;
    }

    @Override
    public List<TaskOutput> run(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs, Optional<LambdaExpression> endLoopCondition) {
        ExecutorService executor = Executors.newCachedThreadPool();

        var allItems = Stream.concat(
                tasks.stream(),
                taskExecutionPlans.stream()
        ).toList();

        if (allItems.isEmpty()) {
            return taskOutputs;
        }

        List<TaskOutput> threadSafeResults = Collections.synchronizedList(taskOutputs);

        List<CompletableFuture<Void>> futures = allItems.stream()
                .map(item -> CompletableFuture.runAsync(() -> {
                    executeItem(item, threadSafeResults);
                }, executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return threadSafeResults;
    }
}
