package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.Value;

import java.security.InvalidParameterException;


public interface Expression {

    static class TypeError extends InvalidParameterException {
        TypeError(String message){super(message);}
    } ;

    Value eval(ChannelState trace, AttributeState memory);
    boolean checkCondition(ChannelState trace, AttributeState memory);
}
