package org.example.dto;

import org.example.agents.BooleanValue;
import org.example.agents.Value;

import java.security.InvalidParameterException;
import java.util.List;

public class DotExpression extends Expression{
    final List<String> chain ;

    public DotExpression(List<String> chain) {
        if (chain.isEmpty()) throw new InvalidParameterException();
        this.chain = chain;
    }

    @Override
    public String toString() {
        return "DotExpression[" + chain +']';
    }

    static Value trueVal = new BooleanValue(true);
    @Override
    boolean checkCondition(List<TaskOutput> trace) {
        assert (!this.chain.isEmpty());
        String id = this.chain.getLast(); // FIXME

        return trace.stream().anyMatch((t)-> t.channel().name.equals(id) && t.value().equals(trueVal));  // FIXME
    }
}
