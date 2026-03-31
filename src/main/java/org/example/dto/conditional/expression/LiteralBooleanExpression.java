package org.example.dto.conditional.expression;

import org.example.dto.task.AgentTaskOutput;

import java.util.List;

public class LiteralBooleanExpression extends Expression {

    final boolean value;

    LiteralBooleanExpression(Boolean b) {
        this.value = b;
    }


    @Override
    public boolean checkCondition(List<AgentTaskOutput> trace) {
        return this.value;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }
}
