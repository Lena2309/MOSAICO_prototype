package org.example.dto.conditional.expression;

import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.BooleanValue;
import org.example.dto.task.output.Value;

import java.security.InvalidParameterException;
import java.util.List;

public class DotExpression extends Expression {
    static final Value trueVal = new BooleanValue(true);
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
    public boolean checkCondition(List<AgentTaskOutput> trace) {
        assert (this.chain.size()>1);
        String channelName = this.chain.getLast();
        String previous = this.chain.get(this.chain.size()-2);

        return trace.stream().anyMatch((t) -> t.channel().getName().equals(channelName) && t.value().equals(trueVal) && t.task().getTaskName().equals(previous));
    }
}
