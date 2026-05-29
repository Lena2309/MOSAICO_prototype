package org.example.dto.conditional.expression;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.task.output.value.MultipleValue;
import org.example.dto.task.output.value.Value;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionInvocation implements Expression {

    static String[] supported = { "SequenceFunctions::including" };

    final String name;
    final List<Expression> parameters;

    @Override
    public Value eval(ChannelState trace, AttributeState memory) {
        List<Value> values = new ArrayList<>();
        //map
        for (Expression e : parameters) values.add(e.eval(trace, memory));

        switch (this.name) {
            case "SequenceFunctions::including" -> {
                assert (values.size()==2);
                var collection = (MultipleValue) values.get(0);
                var addedValue = values.get(1);
                return collection.functionalAdd(addedValue);
            }
            default -> throw new InvalidParameterException("Unrecognized function: " + this.name);
        }
    }

    public FunctionInvocation(String name, List<Expression> parameters) {
        if (!Arrays.asList(supported).contains(name))
            throw new InvalidParameterException("Unrecognized function: " + name);
        this.name = name ;
        this.parameters = parameters ;
    }
}
