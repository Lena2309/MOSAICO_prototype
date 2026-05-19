package org.example.dto.conditional.expression;


import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.BooleanValue;
import org.example.dto.task.output.value.Value;

import java.security.InvalidParameterException;

public abstract class BinopExpression implements Expression {
    final Expression e1, e2;

    public BinopExpression(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    abstract Value op(Value v1, Value v2);

    public Value eval(ChannelState trace, AttributeState memory){
        Value v1 = e1.eval(trace, memory);
        Value v2 = e2.eval(trace, memory);
        return op(v1,v2);
    }

    public boolean checkCondition(ChannelState trace, AttributeState memory){
        Value v = this.eval(trace, memory);
        if (v instanceof BooleanValue b)
            return b.value();
        else
            throw new TypeError("This is not a boolean.");
    }
}
