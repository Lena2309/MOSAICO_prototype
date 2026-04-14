package org.example.dto.step;

import org.example.dto.task.AgentTask;
import org.example.dto.task.AgentTaskOutput;

import java.util.List;
import java.util.Optional;

public class Step {
    private final AgentTask agentTask;
    private Optional<Step> nextStep;

    public Step(AgentTask agentTask) {
        this(agentTask, Optional.empty());
    }

    public Step(Optional<Step> nextStep) {
        this(null, nextStep);
    }

    public Step(AgentTask agentTask, Optional<Step> nextStep) {
        this.agentTask = agentTask;
        this.nextStep = nextStep;
    }

    public Optional<Step> getNextStep() {
        return nextStep;
    }

    public void setNextStep(Step nextStep) {
        this.nextStep = Optional.of(nextStep);
    }

    public AgentTask getAgentTask() {
        return agentTask;
    }

    public void execute(List<AgentTaskOutput> taskDependencies) {
        var optionalTaskOutput = this.executeTask(taskDependencies);
        if (!optionalTaskOutput.isEmpty()) {
            taskDependencies.addAll(optionalTaskOutput);
        }
        System.out.println("    Task " + this.agentTask.getTaskName() + " has been executed successfully.");
    }

    private List<AgentTaskOutput> executeTask(List<AgentTaskOutput> taskDependencies) {
        return this.agentTask.execute(taskDependencies);
    }

    // Helper so steps can identify themselves to the next step
    protected String getStepName() {
        return this.agentTask != null ? this.agentTask.getTaskName() : "Unknown Task";
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

        if (this.agentTask != null) {
            sb.append(this.agentTask.toString());
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
