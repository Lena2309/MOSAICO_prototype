package org.example.dto;

import org.example.agents.BooleanValue;
import org.example.agents.Value;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class LiteralBooleanExpression extends Expression{

    final boolean value;

    LiteralBooleanExpression(Boolean b){
        this.value = b;
    }


    @Override
    boolean checkCondition(List<TaskOutput> trace) {
        return this.value ;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }
}
