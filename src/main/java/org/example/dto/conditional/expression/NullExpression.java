package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.NullValue;
import org.example.dto.task.output.value.Value;

public class NullExpression implements Expression {
    NullExpression() {
    }

    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        return new NullValue();
    }

    @Override
    public boolean checkCondition(ChannelState trace, AttributeState memory) {
        throw new TypeError("Null values cannot be used as booleans.");
    }

    @Override
    public String toString() {
        return "null";
    }
}
