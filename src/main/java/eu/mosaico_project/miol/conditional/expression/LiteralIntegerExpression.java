package eu.mosaico_project.miol.conditional.expression;

import eu.mosaico_project.miol.AttributeState;
import eu.mosaico_project.miol.ChannelState;
import eu.mosaico_project.miol.task.output.value.IntegerValue;
import eu.mosaico_project.miol.task.output.value.Value;


public class LiteralIntegerExpression implements Expression {
    final int value;

    LiteralIntegerExpression(Integer b) {
        this.value = b;
    }

    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        return new IntegerValue(this.value);
    }

    @Override
    public boolean checkCondition(ChannelState trace, AttributeState memory) {
        throw new TypeError("Integer values cannot be used as booleans.");
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
