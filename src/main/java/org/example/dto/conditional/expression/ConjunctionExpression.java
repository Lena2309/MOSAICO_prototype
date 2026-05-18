package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.BooleanValue;
import org.example.dto.task.output.value.Value;


public class ConjunctionExpression implements Expression {
    final Expression e1, e2;

    public ConjunctionExpression(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return "CONJ: " + e1.toString() + " & " + e2.toString();
    }

    @Override
    public boolean checkCondition(ChannelState trace, AttributeState memory) {
        return e1.checkCondition(trace, memory) && e2.checkCondition(trace, memory);
    }

    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        return new BooleanValue(this.checkCondition(trace, memory));
    }
}
