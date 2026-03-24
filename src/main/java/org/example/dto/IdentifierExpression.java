package org.example.dto;

import org.example.agents.BooleanValue;
import org.example.agents.Value;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class IdentifierExpression extends Expression{

    final String identifier;

    IdentifierExpression(String identifier){
        this.identifier = identifier;
    }


    @Override
    boolean checkCondition(List<TaskOutput> trace) {

            Optional<TaskOutput> t = trace.stream().filter((to)->to.channel().name.equals(this.identifier)).findFirst();
            if (t.isEmpty())
                throw new InvalidParameterException("Field " + this.identifier +" not found in trace.");
            else {
                Value v = t.get().value();
                if (v instanceof BooleanValue b)
                    return b.value();
                else throw new InvalidParameterException("Value with bad type: " + v.getClass() + " instead of BooleanValue." );
            }

    }

    @Override
    public String toString() {
        return "IdentifierExpression{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
