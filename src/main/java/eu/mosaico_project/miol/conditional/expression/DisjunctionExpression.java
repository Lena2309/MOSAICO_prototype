package eu.mosaico_project.miol.conditional.expression;

import eu.mosaico_project.miol.task.output.value.BooleanValue;
import eu.mosaico_project.miol.task.output.value.Value;


public class DisjunctionExpression extends BinopExpression {

    public DisjunctionExpression(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public String toString() {
        return "DISJ: " + e1.toString() + " | " + e2.toString();
    }


    @Override
    Value op(Value v1, Value v2) {
        if ((v1 instanceof BooleanValue b1) && (v2 instanceof BooleanValue b2))
            return new BooleanValue(b1.value() || b2.value());
        else throw new TypeError("Not booleans (||).");
    }

}
