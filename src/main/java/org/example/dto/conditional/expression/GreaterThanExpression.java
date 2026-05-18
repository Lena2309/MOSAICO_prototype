package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.BooleanValue;
import org.example.dto.task.output.value.IntegerValue;
import org.example.dto.task.output.value.Value;

import java.security.InvalidParameterException;


public class GreaterThanExpression implements Expression {
    final Expression e1, e2;

    public GreaterThanExpression(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return "> : " + e1.toString() + " | " + e2.toString();
    }

    @Override
    public Value eval(ChannelState trace, AttributeState memory){
        return new BooleanValue(this.checkCondition(trace, memory));
    }

    @Override
    public boolean checkCondition(ChannelState trace, AttributeState memory) {
        Value v1 = e1.eval(trace, memory);
        Value v2 = e2.eval(trace, memory);
        if ( (v1 instanceof IntegerValue i1) && (v2 instanceof IntegerValue i2) )
            return (i1.value > i2.value);
        else throw new InvalidParameterException("Type Error : only integer values can be compared with > .");

    }
}
