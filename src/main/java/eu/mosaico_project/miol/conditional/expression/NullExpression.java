package eu.mosaico_project.miol.conditional.expression;

import eu.mosaico_project.miol.AttributeState;
import eu.mosaico_project.miol.ChannelState;
import eu.mosaico_project.miol.task.output.value.MultipleValue;
import eu.mosaico_project.miol.task.output.value.Value;

public class NullExpression implements Expression {
    NullExpression() {
    }

    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        return new MultipleValue();
    }

    @Override
    public boolean checkCondition(ChannelState trace, AttributeState memory) {
        throw new TypeError("Null/Empty values cannot be used as booleans.");
    }

    @Override
    public String toString() {
        return "null / empty ";
    }
}
