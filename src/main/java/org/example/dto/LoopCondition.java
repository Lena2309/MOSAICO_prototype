package org.example.dto;



import java.security.InvalidParameterException;
import java.util.List;

public record LoopCondition(LoopKind kind, Expression condition) {

    /** @param trace is the reference to resolve names in the expression. */
    public boolean testContinue(List<TaskOutput> trace){
        System.out.println("Tested condition: " + this.condition.toString());
        System.out.println("Context: " + trace.toString());
        boolean b ;
        try { b = this.condition.checkCondition(trace); }
        catch (InvalidParameterException e) {
            System.out.println("(Could not evaluate expression, fallback to false. ["+e.getMessage() +"])");
            b = false ;
        }
        System.out.println("Evaluation of loop condition: " + b);
        return switch (this.kind) {
            case LoopUntil -> !b;
            case While -> b;
        };
    }
}
