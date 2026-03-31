package org.example.dto.conditional;

import org.example.dto.conditional.expression.Expression;
import org.example.dto.task.AgentTaskOutput;

import java.security.InvalidParameterException;
import java.util.List;

public class Condition {
    protected final List<Expression> conditions;

    public Condition(Expression condition) {
        this(List.of(condition));
    }

    public Condition(List<Expression> conditions) {
        this.conditions = conditions;
    }

    /**
     * @param trace is the reference to resolve names in the expression.
     */
    public boolean evaluate(List<AgentTaskOutput> trace) {
        System.out.println("Tested condition: " + this.conditions.toString());
        System.out.println("Context: " + trace.toString());
        var b = true;

        try {
            for (var condition : this.conditions) {
                b = b && condition.checkCondition(trace);
                if (!b) break;
            }
        } catch (InvalidParameterException e) {
            System.out.println("(Could not evaluate expression, fallback to false. [" + e.getMessage() + "])");
        }

        System.out.println("[DEBUG] Evaluation of loop condition: " + b);
        return b;
    }
}
