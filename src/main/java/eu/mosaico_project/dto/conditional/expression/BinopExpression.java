package eu.mosaico_project.dto.conditional.expression;


import eu.mosaico_project.dto.AttributeState;
import eu.mosaico_project.dto.ChannelState;
import eu.mosaico_project.dto.task.output.value.Value;


public abstract class BinopExpression implements Expression {
    final Expression e1, e2;

    public BinopExpression(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    abstract Value op(Value v1, Value v2);

    public Value eval(ChannelState trace, AttributeState memory){
        Value v1 = e1.eval(trace, memory);
        Value v2 = e2.eval(trace, memory);
        return op(v1,v2);
    }

}
