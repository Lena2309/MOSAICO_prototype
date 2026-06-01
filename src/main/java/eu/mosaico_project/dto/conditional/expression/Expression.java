package eu.mosaico_project.dto.conditional.expression;

import eu.mosaico_project.dto.AttributeState;
import eu.mosaico_project.dto.ChannelState;
import eu.mosaico_project.dto.task.output.value.BooleanValue;
import eu.mosaico_project.dto.task.output.value.Value;

import java.security.InvalidParameterException;


public interface Expression {

    class TypeError extends InvalidParameterException {
        TypeError(String message){super(message);}
    }

    Value eval(ChannelState trace, AttributeState memory);

    default boolean checkCondition(ChannelState trace, AttributeState memory) {
        Value v = this.eval(trace, memory);
        if (v instanceof BooleanValue b)
            return b.value();
        else
            throw new TypeError("Value with bad type: " + v.getClass() + " instead of BooleanValue.");
    }
}
