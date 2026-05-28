package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.BooleanValue;
import org.example.dto.task.output.value.Value;

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
