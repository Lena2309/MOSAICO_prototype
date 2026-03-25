package org.example.dto.expression;

import org.example.dto.TaskOutput;
import org.example.dto.output.BooleanValue;
import org.example.dto.output.Value;

import java.security.InvalidParameterException;
import java.util.List;

public class DotExpression extends Expression {
    static Value trueVal = new BooleanValue(true);
    final List<String> chain;

    public DotExpression(List<String> chain) {
        if (chain.isEmpty()) throw new InvalidParameterException();
        this.chain = chain;
    }

    @Override
    public String toString() {
        return "DotExpression[" + chain + ']';
    }

    @Override
    public boolean checkCondition(List<TaskOutput> trace) {
        assert (!this.chain.isEmpty());
        String id = this.chain.getLast(); // FIXME

        return trace.stream().anyMatch((t) -> t.channel().name.equals(id) && t.value().equals(trueVal));  // FIXME
    }
}
