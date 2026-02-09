package org.example.dto;

import jakarta.el.LambdaExpression;

import java.util.List;
import java.util.Optional;

public record TaskExecutionPlan(int executionOrder, List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, WorkflowType workflowType, Optional<LambdaExpression> endLoopCondition) implements OrderedMOSAICOExecution {
}
