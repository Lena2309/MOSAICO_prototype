package org.example.dto.conditional.expression;

import org.example.dto.task.output.TaskOutput;
import org.example.dto.task.output.value.Value;

import java.util.List;

public class AssignExpression extends Expression {
    @Override
    public boolean checkCondition(List<TaskOutput> trace) {
        return false;
    }

    public Value evaluate(List<TaskOutput> trace) {
        return null;
    }
}
