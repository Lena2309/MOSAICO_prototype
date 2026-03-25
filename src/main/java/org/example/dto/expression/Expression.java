package org.example.dto.expression;

import org.example.dto.TaskOutput;

import java.util.List;

public abstract class Expression {
    public abstract boolean checkCondition(List<TaskOutput> trace);
}
