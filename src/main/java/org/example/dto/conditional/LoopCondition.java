package org.example.dto.conditional;


import org.example.dto.conditional.expression.Expression;
import org.example.dto.task.AgentTaskOutput;

import java.security.InvalidParameterException;
import java.util.List;

public class LoopCondition extends Condition {
    private final LoopKind kind;

    public LoopCondition(LoopKind kind, Expression condition) {
        super(condition);
        this.kind = kind;
    }

    public LoopCondition(LoopKind kind, List<Expression> conditions) {
        super(conditions);
        this.kind = kind;
    }

    @Override
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
            b = false;
        }

        System.out.println("[DEBUG] Evaluation of loop condition: " + b);
        return switch (this.kind) {
            case UNTIL -> !b;
            case WHILE -> b;
        };
    }
}
