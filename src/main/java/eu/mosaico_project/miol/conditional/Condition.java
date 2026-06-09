package eu.mosaico_project.miol.conditional;

import eu.mosaico_project.miol.AttributeState;
import eu.mosaico_project.miol.ChannelState;
import eu.mosaico_project.miol.conditional.expression.Expression;

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
