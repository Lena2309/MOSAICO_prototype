package eu.mosaico_project.dto.conditional;

import eu.mosaico_project.dto.AttributeState;
import eu.mosaico_project.dto.ChannelState;
import eu.mosaico_project.dto.conditional.expression.Expression;

public record Condition(Expression expression) {

    /**
     * @param trace is the reference to resolve names in the expression.
     */
    public boolean evaluate(ChannelState trace, AttributeState memory) {
        System.out.println("[DEBUG] Tested condition: " + this.expression.toString());
        var b = expression.checkCondition(trace, memory);
        System.out.println("[DEBUG] Evaluation of condition: " + b);
        return b;
    }
}
