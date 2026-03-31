package org.example.dto.step;

import org.example.dto.conditional.Condition;
import org.example.dto.task.AgentTaskOutput;

import java.util.List;
import java.util.Optional;

public class IfStep extends Step {
    private final Step thenStep;
    private final Optional<Step> elseStep;
    private final Condition ifCondition;

    public IfStep(Step thenStep, Condition ifCondition, Optional<Step> nextStep) {
        this(thenStep, Optional.empty(), ifCondition, nextStep);
    }

    public IfStep(Step thenStep, Optional<Step> elseStep, Condition condition, Optional<Step> nextStep) {
        super(nextStep);
        this.thenStep = thenStep;
        this.elseStep = elseStep;
        this.ifCondition = condition;
    }

    @Override
    public void execute(List<AgentTaskOutput> agentTaskOutputs) {
        if (ifCondition.evaluate(agentTaskOutputs)) {
            thenStep.execute(agentTaskOutputs);
        } else {
            elseStep.ifPresent(elseStep -> elseStep.execute(agentTaskOutputs));
        }
    }
}
