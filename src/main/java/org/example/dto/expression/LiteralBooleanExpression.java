package org.example.dto.expression;

import org.example.dto.TaskOutput;

import java.util.List;

public class LiteralBooleanExpression extends Expression {

    final boolean value;

    LiteralBooleanExpression(Boolean b) {
        this.value = b;
    }


    @Override
    public boolean checkCondition(List<TaskOutput> trace) {
        return this.value;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }
}
