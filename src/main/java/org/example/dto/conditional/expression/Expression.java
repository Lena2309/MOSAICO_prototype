package org.example.dto.conditional.expression;

import org.example.dto.State;
import org.example.dto.task.output.value.Value;


public interface Expression {
    Value eval(State trace);
    boolean checkCondition(State trace);
}
