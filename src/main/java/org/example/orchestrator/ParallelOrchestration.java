package org.example.orchestrator;

import org.example.agents.CollaborationAgent;
import org.example.dto.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ParallelOrchestration(CollaborationAgent collaborationAgent) implements Orchestrator {
    @Override
    public WorkflowType getWorkflowType() {
        return WorkflowType.PARALLEL;
    }

    @Override
    public List<TaskOutput> run(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs) {
        ExecutorService executor = Executors.newCachedThreadPool();

            var allItems = Stream.concat(
                    safeStream(tasks),
                    safeStream(taskExecutionPlans)
            ).toList();

            if (allItems.isEmpty()) {
                return List.of();
            }

            List<TaskOutput> threadSafeResults = Collections.synchronizedList(taskOutputs);

            List<CompletableFuture<Void>> futures = allItems.stream()
                    .map(item -> CompletableFuture.runAsync(() -> {
                        // Pass the thread-safe list so executeItem can update it
                        executeItem(this.collaborationAgent, item, threadSafeResults);
                    }, executor))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            return threadSafeResults;
        }

        private <T> Stream<T> safeStream(List<T> list) {
            return list == null ? Stream.empty() : list.stream();
        }
}
