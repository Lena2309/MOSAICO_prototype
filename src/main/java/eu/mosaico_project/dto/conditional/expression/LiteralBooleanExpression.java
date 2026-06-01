package eu.mosaico_project.dto.conditional.expression;

import eu.mosaico_project.dto.AttributeState;
import eu.mosaico_project.dto.ChannelState;
import eu.mosaico_project.dto.task.output.value.BooleanValue;
import eu.mosaico_project.dto.task.output.value.Value;


public class LiteralBooleanExpression implements Expression {
    final boolean value;

    LiteralBooleanExpression(Boolean b) {
        this.value = b;
    }

    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        return new BooleanValue(this.value);
    }

    @Override
    public boolean checkCondition(ChannelState trace, AttributeState memory) {
        return this.value;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }
}
