package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.StringValue;
import org.example.dto.task.output.value.Value;

public class LiteralStringExpression implements Expression {
    final String value;

    LiteralStringExpression(String str) {
        this.value = str;
    }

    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        return new StringValue(this.value);
    }

    @Override
    public boolean checkCondition(ChannelState trace, AttributeState memory) {
        throw new TypeError("Integer values cannot be used as booleans.");
    }

    @Override
    public String toString() {
        return this.value;
    }
}