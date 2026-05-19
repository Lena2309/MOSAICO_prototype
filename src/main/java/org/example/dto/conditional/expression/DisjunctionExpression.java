package org.example.dto.conditional.expression;

import org.example.dto.task.output.value.BooleanValue;
import org.example.dto.task.output.value.Value;

import java.security.InvalidParameterException;


public class DisjunctionExpression extends BinopExpression {

    public DisjunctionExpression(Expression e1, Expression e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "DISJ: " + e1.toString() + " | " + e2.toString();
    }


    @Override
    Value op(Value v1, Value v2){
        if ( (v1 instanceof BooleanValue b1) && (v2 instanceof BooleanValue b2) )
            return new BooleanValue(b1.value() || b2.value());
        else throw new InvalidParameterException("Type Error: not booleans (||).");
    }

}
