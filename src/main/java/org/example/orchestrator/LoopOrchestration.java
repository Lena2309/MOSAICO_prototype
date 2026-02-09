package org.example.orchestrator;

import org.example.agents.CollaborationAgent;
import org.example.dto.Task;
import org.example.dto.TaskExecutionPlan;
import org.example.dto.TaskOutput;
import org.example.dto.WorkflowType;

import java.util.List;

public record LoopOrchestration(CollaborationAgent collaborationAgent) implements Orchestrator {
    @Override
    public WorkflowType getWorkflowType() {
        return WorkflowType.LOOP;
    }

    @Override
    public List<TaskOutput> run(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs) {
        return List.of();
    }
}
