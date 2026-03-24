package org.example.dto;

import java.util.List;
import java.util.UUID;

public class TaskExecutionPlan implements OrderedMOSAICOExecution {
    private final String id = UUID.randomUUID().toString();
    private final String name;
    private final int executionOrder;
    private final WorkflowType workflowType;

    private List<Task> tasks;
    private List<TaskExecutionPlan> taskExecutionPlans;
    private final LoopCondition endLoopCondition;

    public TaskExecutionPlan(int executionOrder,
                             List<Task> tasks,
                             List<TaskExecutionPlan> taskExecutionPlans,
                             WorkflowType workflowType,
                             LoopCondition endLoopCondition) {
        this(executionOrder, "unnamed", tasks, taskExecutionPlans, workflowType, endLoopCondition);
    }

    public TaskExecutionPlan(int executionOrder,
                             String name,
                             List<Task> tasks,
                             List<TaskExecutionPlan> taskExecutionPlans,
                             WorkflowType workflowType,
                             LoopCondition endLoopCondition) {
        this.executionOrder = executionOrder;
        this.name = name;
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

    public LoopCondition getEndLoopCondition() {
        return endLoopCondition;
    }


    // Add Methods
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void addTaskExecutionPlan(TaskExecutionPlan plan) {
        this.taskExecutionPlans.add(plan);
    }


}
