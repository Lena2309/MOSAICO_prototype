package org.example.dto.conditional.expression;

import org.example.dto.task.output.value.BooleanValue;
import org.example.dto.task.output.value.Value;

public class EqualExpression extends BinopExpression {
    public EqualExpression(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public String toString() {
        return "EQ : " + e1.toString() + " == " + e2.toString();
    }

    @Override
    Value op(Value v1, Value v2) {
        return new BooleanValue(v1.equals(v2));
    }
}
