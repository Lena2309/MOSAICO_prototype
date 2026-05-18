package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.BooleanValue;
import org.example.dto.task.output.value.Value;


public class LiteralBooleanExpression implements Expression {
    final boolean value;

    LiteralBooleanExpression(Boolean b) {
        this.value = b;
    }

    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        return new BooleanValue(this.value);
    }

    @Override
    public boolean checkCondition(ChannelState trace, AttributeState memory) {
        return this.value;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }
}
