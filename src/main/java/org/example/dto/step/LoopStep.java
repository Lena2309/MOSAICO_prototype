package org.example.dto.step;

import org.example.dto.conditional.LoopCondition;
import org.example.dto.task.AgentTaskOutput;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class LoopStep extends Step {
    private final List<Step> body;
    private final LoopCondition endCondition;
    private final String name = "loop";

    public LoopStep(List<Step> body, LoopCondition endCondition, Optional<Step> nextStep) {
        super(nextStep);
        this.body = body;
        this.endCondition = endCondition;
    }

    public List<Step> getBody() {
        return body;
    }

    public LoopCondition getEndCondition() {
        return endCondition;
    }

    @Override
    public void execute(List<AgentTaskOutput> agentTaskOutputs) {
        System.out.println("--- Starting Loop Step Execution ---");
        if (this.body.isEmpty()) {
            return;
        }

        if (this.endCondition == null)
            throw new InvalidParameterException("Missing loop condition.");
        else {
            var result = endCondition.evaluate(agentTaskOutputs); // fixme : inline
            System.out.println("Result of evaluation of loop condition:" + result);
            while (result && agentTaskOutputs.size() < 50) {
                for (var step : body) {
                    step.execute(agentTaskOutputs);
                }
                result = endCondition.evaluate(agentTaskOutputs);

            }
            if (!result) System.out.println("Loop ended because loop Condition satisfied.");
            if (!(agentTaskOutputs.size() < 50)) System.out.println("[DEBUG MODE] Loop ended because trace too big.");
        }

        System.out.println("--- Finished Parallel Loop Execution ---");
    }
}
