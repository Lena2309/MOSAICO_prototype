package org.example.dto.conditional.expression;

import org.example.dto.task.AgentTaskOutput;

import java.util.List;

public class DisjunctionExpression extends Expression {
    final Expression e1, e2;

    public DisjunctionExpression(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return "DISJ: " + e1.toString() + " | " + e2.toString();
    }

    @Override
    public boolean checkCondition(List<AgentTaskOutput> trace) {
        return e1.checkCondition(trace) || e2.checkCondition(trace);
    }
}
