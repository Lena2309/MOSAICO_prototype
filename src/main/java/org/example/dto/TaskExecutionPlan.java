package org.example.dto;

import java.util.List;

public record TaskExecutionPlan(int executionOrder, List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, WorkflowType workflowType) implements OrderedMOSAICOExecution {
}
