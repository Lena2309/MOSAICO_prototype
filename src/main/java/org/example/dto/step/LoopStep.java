package org.example.dto.step;

import org.example.dto.conditional.Condition;
import org.example.dto.conditional.LoopKind;
import org.example.dto.task.AgentTaskOutput;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class LoopStep extends Step {
    private final Step headStep;
    private final Condition endCondition;
    private final String name = "loop";
    private final LoopKind kind;

    public LoopStep(Step headStep, Condition endCondition, LoopKind loopKind, Optional<Step> nextStep) {
        super(nextStep);
        this.headStep = headStep;
        this.endCondition = endCondition;
        this.kind = loopKind;
    }

    public Step getHeadStep() {
        return headStep;
    }

    public Condition getEndCondition() {
        return endCondition;
    }

    @Override
    public void execute(List<AgentTaskOutput> agentTaskOutputs) {
        System.out.println("--- Starting Loop Step Execution ---");
        if (this.headStep == null) {
            return;
        }

        if (this.endCondition == null)
            throw new InvalidParameterException("Missing loop condition.");
        else {
            var result = endCondition.evaluate(agentTaskOutputs);
            result = switch (this.kind) {
                case UNTIL -> !result;
                case WHILE -> result;
            };

            System.out.println("Result of evaluation of loop condition:" + result);
            while (result && agentTaskOutputs.size() < 50) {
                var currentStep = this.headStep;
                while (currentStep != null) {
                    currentStep.execute(agentTaskOutputs);
                    currentStep = currentStep.getNextStep().orElse(null);
                }
                result = endCondition.evaluate(agentTaskOutputs);
            }
            if (!result) System.out.println("Loop ended because loop Condition satisfied.");
            if (!(agentTaskOutputs.size() < 50)) System.out.println("[DEBUG MODE] Loop ended because trace too big.");
        }

        System.out.println("--- Finished Parallel Loop Execution ---");
    }
}
