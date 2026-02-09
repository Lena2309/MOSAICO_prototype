package org.example.orchestrator;

import org.example.agents.CollaborationAgent;
import org.example.dto.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public record SequentialOrchestrator(CollaborationAgent collaborationAgent) implements Orchestrator {
    @Override
    public WorkflowType getWorkflowType() {
        return WorkflowType.SEQUENTIAL;
    }

    @Override
    public List<TaskOutput> run(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs) {
        System.out.println("--- Starting Sequential Plan Execution ---");

        var executionQueue = Stream.concat(tasks.stream(), taskExecutionPlans.stream())
                .sorted(Comparator.comparingInt(OrderedMOSAICOExecution::executionOrder))
                .toList();

        for (OrderedMOSAICOExecution executable : executionQueue) {
            executeItem(collaborationAgent, executable, taskOutputs);
        }

        System.out.println("--- Finished Sequential Plan Order ---");
        return taskOutputs;
    }
}