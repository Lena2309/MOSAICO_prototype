package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.IntegerValue;
import org.example.dto.task.output.value.Value;

import java.security.InvalidParameterException;


public class LiteralIntegerExpression implements Expression {
    final int value;

    LiteralIntegerExpression(Integer b) {
        this.value = b;
    }

    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        return new IntegerValue(this.value);
    }

    @Override
    public boolean checkCondition(ChannelState trace, AttributeState memory) {
        throw new InvalidParameterException("Type Error : Integer values cannot be used as booleans.");
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
