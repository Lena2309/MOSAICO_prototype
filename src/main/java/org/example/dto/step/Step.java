package org.example.dto.step;

import org.example.dto.task.Task;
import org.example.dto.task.output.TaskOutput;

import java.util.List;
import java.util.Optional;

public class Step {
    private final Task task;
    private Optional<Step> nextStep;

    public Step(Task task) {
        this(task, Optional.empty());
    }

    public Step(Optional<Step> nextStep) {
        this(null, nextStep);
    }

    public Step(Task task, Optional<Step> nextStep) {
        this.task = task;
        this.nextStep = nextStep;
    }

    public Optional<Step> getNextStep() {
        return nextStep;
    }

    public void setNextStep(Step nextStep) {
        this.nextStep = Optional.of(nextStep);
    }

    public Task getTask() {
        return task;
    }

    public void execute(List<TaskOutput> taskDependencies) {
        var optionalTaskOutput = this.executeTask(taskDependencies);
        if (!optionalTaskOutput.isEmpty()) {
            taskDependencies.addAll(optionalTaskOutput);
        }
        System.out.println("[LOG] Task " + this.task.getTaskName() + " has been executed successfully.");
        System.out.println("[LOG] Trace: " + taskDependencies);
    }

    private List<TaskOutput> executeTask(List<TaskOutput> taskDependencies) {
        return this.task.execute(taskDependencies);
    }

    // Helper so steps can identify themselves to the next step
    protected String getStepName() {
        return this.task != null ? this.task.getTaskName() : "Unknown Task";
    }

    @Override
    public String toString() {
        return "\n" + buildString("", new java.util.HashSet<>(), new java.util.concurrent.atomic.AtomicInteger(1), "None");
    }

    protected String buildString(String indent, java.util.Set<Step> visited, java.util.concurrent.atomic.AtomicInteger counter, String prevName) {
        if (visited.contains(this)) {
            return indent + "[Cycle Detected] -> " + getStepName() + "\n";
        }
        visited.add(this);

        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(counter.getAndIncrement()).append(". |- [Step] ");

        if (this.task != null) {
            sb.append(this.task.toString());
        } else {
            sb.append("Empty Task");
        }

        if (prevName != null && !prevName.equals("None")) {
            sb.append(" (next of ").append(prevName).append(")");
        }
        sb.append("\n");

        if (this.nextStep != null && this.nextStep.isPresent()) {
            sb.append(this.nextStep.get().buildString(indent, visited, counter, this.getStepName()));
        }
        return sb.toString();
    }
}
