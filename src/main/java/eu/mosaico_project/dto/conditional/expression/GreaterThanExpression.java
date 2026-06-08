package eu.mosaico_project.dto.conditional.expression;


import eu.mosaico_project.dto.task.output.value.BooleanValue;
import eu.mosaico_project.dto.task.output.value.IntegerValue;
import eu.mosaico_project.dto.task.output.value.Value;


public class GreaterThanExpression extends BinopExpression {

    public GreaterThanExpression(Expression e1, Expression e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "GT : " + e1.toString() + " > " + e2.toString();
    }

    @Override
    Value op(Value v1, Value v2){
        if ( (v1 instanceof IntegerValue i1) && (v2 instanceof IntegerValue i2) )
            return new BooleanValue(i1.value > i2.value);
        else throw new TypeError("Only integer values can be compared with > .");
    }

}
