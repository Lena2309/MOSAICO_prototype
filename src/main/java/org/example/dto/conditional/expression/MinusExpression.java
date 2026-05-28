package org.example.dto.conditional.expression;

import org.example.dto.task.output.value.IntegerValue;
import org.example.dto.task.output.value.Value;


public class MinusExpression extends  BinopExpression {

    public MinusExpression(Expression e1, Expression e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "MINUS : " + e1.toString() + " + " + e2.toString();
    }

    @Override
    Value op(Value v1, Value v2){
        if ( (v1 instanceof IntegerValue i1) && (v2 instanceof IntegerValue i2) )
            return new IntegerValue(i1.value - i2.value);
        else throw new TypeError("Only integer values can be added with - .");
    }

}
