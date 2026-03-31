package org.example.dto.conditional.expression;

import org.example.dto.task.AgentTaskOutput;

import java.util.List;

public abstract class Expression {
    public abstract boolean checkCondition(List<AgentTaskOutput> trace);
}
