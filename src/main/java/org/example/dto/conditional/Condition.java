package org.example.dto.conditional;

import org.example.dto.State;
import org.example.dto.conditional.expression.Expression;

public record Condition(Expression expression) {

    /**
     * @param trace is the reference to resolve names in the expression.
     */
    public boolean evaluate(State trace) {
        System.out.println("[DEBUG] Tested condition: " + this.expression.toString());
        var b = expression.checkCondition(trace);
        System.out.println("[DEBUG] Evaluation of condition: " + b);
        return b;
    }
}
