package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.Value;


public interface Expression {
    Value eval(ChannelState trace, AttributeState memory);
    boolean checkCondition(ChannelState trace, AttributeState memory);
}
