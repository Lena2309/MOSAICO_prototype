package org.example.dto;

import org.example.agents.BooleanValue;
import org.example.agents.Value;

import java.security.InvalidParameterException;
import java.util.List;

public class ConjunctionExpression extends Expression{
    final Expression e1, e2 ;

    public ConjunctionExpression(Expression e1, Expression e2) {
        this.e1 = e1 ;
        this.e2 = e2 ;
    }

    @Override
    public String toString() {
        return e1.toString() + "&" + e2.toString();
    }

    @Override
    boolean checkCondition(List<TaskOutput> trace) {

        return e1.checkCondition(trace) && e2.checkCondition(trace) ;
    }
}
