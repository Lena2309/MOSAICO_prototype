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
        System.out.println("--- Starting Sequential Step Execution ---");
        var optionalTaskOutput = this.executeTask(taskDependencies);
        if (optionalTaskOutput.isPresent()) {
            taskDependencies.add(optionalTaskOutput.get());
        } else {
            System.out.println("[WARNING] No task output for this step.");
        }
        System.out.println("--- Finished Parallel Step Execution ---");
    }

    private Optional<AgentTaskOutput> executeTask(List<AgentTaskOutput> taskDependencies) {
        return this.agentTask.execute(taskDependencies);
    }
}
