package org.example.dto.conditional.expression;

import org.example.dto.task.output.TaskOutput;

import java.util.List;

public abstract class Expression {
    public abstract boolean checkCondition(List<TaskOutput> trace);
}
