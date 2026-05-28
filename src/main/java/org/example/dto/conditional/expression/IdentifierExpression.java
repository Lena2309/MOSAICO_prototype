package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.Value;

import java.security.InvalidParameterException;
import java.util.Optional;

public class IdentifierExpression implements Expression {
    final String identifier;

    IdentifierExpression(String identifier) {
        this.identifier = identifier;
    }


    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        Optional<Value> t = trace.getFromChannel(this.identifier);
        if (t.isEmpty()) {
            var res = memory.get(this.identifier);
            if (res == null) {
                throw new InvalidParameterException("Field " + this.identifier + " not found in state.");
            }
            else return res ;
        }
        else return t.get();
    }

    @Override
    public String toString() {
        return "IdentifierExpression{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
