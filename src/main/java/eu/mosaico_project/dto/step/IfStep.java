package eu.mosaico_project.dto.step;

import eu.mosaico_project.dto.AttributeState;
import eu.mosaico_project.dto.ChannelState;
import eu.mosaico_project.dto.conditional.Condition;

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
    public void execute(ChannelState agentTaskOutputs, AttributeState memory) {
        if (ifCondition.evaluate(agentTaskOutputs, memory)) {
            thenStep.execute(agentTaskOutputs, memory);
        } else {
            elseStep.ifPresent(elseStep -> elseStep.execute(agentTaskOutputs, memory));
        }
    }

    @Override
    protected String getStepName() {
        return "IfStep";
    }

    @Override
    protected String buildString(String indent, java.util.Set<Step> visited, java.util.concurrent.atomic.AtomicInteger counter, String prevName) {
        if (visited.contains(this)) return indent + "[Cycle Detected] -> IfStep\n";
        visited.add(this);

        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(counter.getAndIncrement()).append(". |- [IfStep] Condition: ")
                .append(ifCondition != null ? ifCondition.toString() : "None");

        if (prevName != null && !prevName.equals("None")) {
            sb.append(" (next of ").append(prevName).append(")");
        }
        sb.append("\n");

        sb.append(indent).append("   |-- Then:\n");
        if (this.thenStep != null) {
            sb.append(this.thenStep.buildString(indent + "       ", new java.util.HashSet<>(visited), counter, "IfStep (Then)"));
        }

        if (this.elseStep != null && this.elseStep.isPresent()) {
            sb.append(indent).append("   |-- Else:\n");
            sb.append(this.elseStep.get().buildString(indent + "       ", new java.util.HashSet<>(visited), counter, "IfStep (Else)"));
        }

        if (this.getNextStep() != null && this.getNextStep().isPresent()) {
            sb.append(this.getNextStep().get().buildString(indent, visited, counter, this.getStepName()));
        }
        return sb.toString();
    }
}
