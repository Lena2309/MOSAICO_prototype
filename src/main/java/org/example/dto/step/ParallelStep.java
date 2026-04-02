package org.example.dto.step;

import org.example.dto.task.AgentTaskOutput;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelStep extends Step {
    private final List<Step> body;

    public ParallelStep(List<Step> body, Optional<Step> nextStep) {
        super(nextStep);
        this.body = body;
    }

    public List<Step> getBody() {
        return body;
    }

    @Override
    public void execute(List<AgentTaskOutput> agentTaskOutputs) {
        System.out.println("--- Starting Parallel Step Execution ---");
        if (this.body.isEmpty()) {
            return;
        }

        // Ensure the output list can handle concurrent additions from multiple worker threads
        var threadSafeResults = Collections.synchronizedList(agentTaskOutputs);

        // Try-with-resources on the executor ensures proper shutdown and
        // waits for all virtual threads to terminate before exiting the block.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = this.body.stream()
                    .map(step -> CompletableFuture.runAsync(() -> {
                                System.out.println("  [Parallel] Starting: " + step.getAgentTask().getTaskName());
                                step.execute(threadSafeResults);
                                System.out.println("  [Parallel] Completed: " + step.getAgentTask().getTaskName());
                            }, executor)
                            .exceptionally(ex -> {
                                // Capture and log exceptions within threads to prevent silent failures
                                System.err.println("  [Parallel] FAILED: " + step.getAgentTask().getTaskName() + " -> " + ex.getMessage());
                                ex.printStackTrace();
                                return null;
                            }))
                    .toList();

            // Block the main thread until every submitted task reports completion
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        System.out.println("--- Finished Parallel Step Execution ---");
    }

    @Override
    protected String getStepName() {
        return "ParallelStep";
    }

    @Override
    protected String buildString(String indent, java.util.Set<Step> visited, java.util.concurrent.atomic.AtomicInteger counter, String prevName) {
        if (visited.contains(this)) return indent + "[Cycle Detected] -> ParallelStep\n";
        visited.add(this);

        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(counter.getAndIncrement()).append(". |- [ParallelStep] (Branches: ")
                .append(body != null ? body.size() : 0).append(")");

        if (prevName != null && !prevName.equals("None")) {
            sb.append(" (next of ").append(prevName).append(")");
        }
        sb.append("\n");

        if (this.body != null) {
            for (int i = 0; i < body.size(); i++) {
                sb.append(indent).append("   |-- Branch ").append(i + 1).append(":\n");
                sb.append(body.get(i).buildString(indent + "       ", new java.util.HashSet<>(visited), new AtomicInteger(1), "Parallel Split"));
            }
        }

        if (this.getNextStep() != null && this.getNextStep().isPresent()) {
            sb.append(this.getNextStep().get().buildString(indent, visited, counter, this.getStepName()));
        }
        return sb.toString();
    }
}
