package org.example.dto.conditional.expression;

import org.example.dto.task.output.TaskOutput;
import org.example.dto.task.output.value.BooleanValue;
import org.example.dto.task.output.value.Value;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class IdentifierExpression extends Expression {
    final String identifier;

    IdentifierExpression(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean checkCondition(List<TaskOutput> trace) {
        Optional<TaskOutput> t = trace.stream().filter((to) -> to.channel().name().equals(this.identifier)).findFirst();
        if (t.isEmpty())
            throw new InvalidParameterException("Field " + this.identifier + " not found in trace.");
        else {
            Value v = t.get().value();
            if (v instanceof BooleanValue b)
                return b.value();
            else
                throw new InvalidParameterException("Value with bad type: " + v.getClass() + " instead of BooleanValue.");
        }
    }

    @Override
    public String toString() {
        return "IdentifierExpression{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
