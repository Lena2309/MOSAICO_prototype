package eu.mosaico_project.miol.conditional.expression;

import eu.mosaico_project.miol.AttributeState;
import eu.mosaico_project.miol.ChannelState;
import eu.mosaico_project.miol.task.output.value.StringValue;
import eu.mosaico_project.miol.task.output.value.Value;


public class LiteralStringExpression implements Expression {
    final String value;

    LiteralStringExpression(String str) {
        this.value = str;
    }

    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        return new StringValue(this.value);
    }

    @Override
    public boolean checkCondition(ChannelState trace, AttributeState memory) {
        throw new TypeError("String values cannot be used as booleans.");
    }

    @Override
    public String toString() {
        return this.value;
    }
}