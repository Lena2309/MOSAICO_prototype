package org.example.dto;

import jakarta.el.LambdaExpression;

import java.util.List;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskExecutionPlan implements OrderedMOSAICOExecution {
    private final int executionOrder;
    private final WorkflowType workflowType;

    private List<Task> tasks;
    private List<TaskExecutionPlan> taskExecutionPlans;
    private Optional<LambdaExpression> endLoopCondition;

    public TaskExecutionPlan(int executionOrder, WorkflowType workflowType) {
        this(executionOrder, new ArrayList<>(), new ArrayList<>(), workflowType, Optional.empty());
    }

    public TaskExecutionPlan(int executionOrder, WorkflowType workflowType,
                             Optional<LambdaExpression> endLoopCondition) {
        this(executionOrder, new ArrayList<>(), new ArrayList<>(), workflowType, endLoopCondition);
    }

    public TaskExecutionPlan(int executionOrder,
                             List<Task> tasks,
                             List<TaskExecutionPlan> taskExecutionPlans,
                             WorkflowType workflowType,
                             Optional<LambdaExpression> endLoopCondition) {
        this.executionOrder = executionOrder;
        this.tasks = tasks;
        this.taskExecutionPlans = taskExecutionPlans;
        this.workflowType = workflowType;
        this.endLoopCondition = endLoopCondition;
    }

    // Getters
    @Override
    public int getExecutionOrder() {
        return executionOrder;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<TaskExecutionPlan> getTaskExecutionPlans() {
        return taskExecutionPlans;
    }

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public Optional<LambdaExpression> getEndLoopCondition() {
        return endLoopCondition;
    }

    // Add Methods
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void addTaskExecutionPlan(TaskExecutionPlan plan) {
        this.taskExecutionPlans.add(plan);
    }

    public void setEndLoopCondition(Optional<LambdaExpression> endLoopCondition) {
        this.endLoopCondition = endLoopCondition;
    }


}
