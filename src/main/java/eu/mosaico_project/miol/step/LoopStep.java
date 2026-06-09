package eu.mosaico_project.miol.step;

import eu.mosaico_project.miol.AttributeState;
import eu.mosaico_project.miol.ChannelState;
import eu.mosaico_project.miol.conditional.Condition;
import eu.mosaico_project.miol.conditional.LoopKind;

import java.security.InvalidParameterException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class LoopStep extends Step {
    public static final int MAX_TRACE_SIZE = 50;
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
    public void execute(ChannelState agentTaskOutputs, AttributeState memory) {
        System.out.println("--- Starting Loop Step Execution ---");
        if (this.headStep == null) {
            return;
        }

        if (this.endCondition == null)
            throw new InvalidParameterException("Missing loop condition.");
        else {
            boolean shouldContinue;

            // First evaluation
            shouldContinue =
                    switch (this.kind) {
                        case LoopKind.WHILE: {
                            // In a 'while' loop, we first evaluate the condition before the body.
                            yield endCondition.evaluate(agentTaskOutputs, memory);
                        }
                        case LoopKind.UNTIL: {
                            // In a 'until' loop, we first evaluate the body, then the condition.
                            yield true;
                        }
                    };

            // Repetition
            while (shouldContinue && agentTaskOutputs.size() < MAX_TRACE_SIZE) {
                var currentStep = this.headStep;
                while (currentStep != null) {
                    currentStep.execute(agentTaskOutputs, memory);
                    currentStep = currentStep.getNextStep().orElse(null);
                }
                var rawResult = endCondition.evaluate(agentTaskOutputs, memory);
                shouldContinue = switch (this.kind) {
                    case UNTIL -> !rawResult;
                    case WHILE -> rawResult;
                };
            }
            if (!shouldContinue) System.out.println("Loop ended because loop Condition satisfied.");
            if (agentTaskOutputs.size() >= MAX_TRACE_SIZE) {
                System.out.println("[ERROR] Loop ended because trace too large.");
                System.out.println("[ERROR] MAX_TRACE_SIZE = " + MAX_TRACE_SIZE);
                System.out.println("[LOG] Trace = " + agentTaskOutputs);
                throw new RuntimeException("Loop ended because trace too large.");
            }
        }

        System.out.println("--- Finished Loop Step Execution ---");
    }

    @Override
    protected String getStepName() {
        return "LoopStep";
    }

    @Override
    protected String buildString(String indent, java.util.Set<Step> visited, java.util.concurrent.atomic.AtomicInteger counter, String prevName) {
        if (visited.contains(this)) return indent + "[Cycle Detected] -> LoopStep\n";
        visited.add(this);

        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(counter.getAndIncrement()).append(". |- [LoopStep] Kind: ")
                .append(kind)
                .append("; Condition: ")
                .append(endCondition != null ? endCondition.toString() : "None");

        if (prevName != null && !prevName.equals("None")) {
            sb.append(" (next of ").append(prevName).append(")");
        }
        sb.append("\n");

        var subCounter = new AtomicInteger(1);

        if (this.headStep != null) {
            sb.append(this.headStep.buildString(indent + "       ", new java.util.HashSet<>(visited), subCounter, "Loop Start"));
        }

        if (this.getNextStep() != null && this.getNextStep().isPresent()) {
            sb.append(this.getNextStep().get().buildString(indent, visited, counter, this.getStepName()));
        }
        return sb.toString();
    }
}
