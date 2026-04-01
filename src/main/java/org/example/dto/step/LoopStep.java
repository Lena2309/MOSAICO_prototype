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
            boolean shouldContinue;
            if (this.kind == LoopKind.WHILE) {
                shouldContinue = endCondition.evaluate(agentTaskOutputs);
            } else {
                // until loops in SysML are evaluated after the body is executed
                // so they always execute at least once
                shouldContinue = true;
            }

            while (shouldContinue && agentTaskOutputs.size() < 50) {
                System.out.println("Result of evaluation of loop condition:" + shouldContinue);
                var currentStep = this.headStep;
                while (currentStep != null) {
                    currentStep.execute(agentTaskOutputs);
                    currentStep = currentStep.getNextStep().orElse(null);
                }
                var rawResult = endCondition.evaluate(agentTaskOutputs);
                shouldContinue = switch (this.kind) {
                    case UNTIL -> !rawResult;
                    case WHILE -> rawResult;
                };
            }
            if (!shouldContinue) System.out.println("Loop ended because loop Condition satisfied.");
            if (!(agentTaskOutputs.size() < 50)) System.out.println("[DEBUG MODE] Loop ended because trace too big.");
        }

        System.out.println("--- Finished Parallel Loop Execution ---");
    }
}
