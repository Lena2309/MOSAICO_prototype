package org.example.dto.conditional.expression;

import org.example.dto.task.output.TaskOutput;
import org.example.dto.task.output.value.BooleanValue;
import org.example.dto.task.output.value.Value;

import java.security.InvalidParameterException;
import java.util.List;

public class DotExpression extends Expression {
    static final Value trueVal = new BooleanValue(true);
    final String channelName;
    final List<String> otherParents;
    final String taskName;

    public DotExpression(List<String> chain) {
        if (chain.size() < 2) throw new InvalidParameterException();
        this.channelName = chain.getLast();
        this.taskName = chain.get(chain.size() - 2);
        this.otherParents = chain.subList(0, chain.size() - 2);
    }

    static <T> boolean isSuffixOf(List<T> l1, List<T> l2) {
        int s1 = l1.size();
        int s2 = l2.size();
        return l2.subList(s2 - s1, s2).equals(l1);
    }

    @Override
    public String toString() {
        return "DotExpression[" + otherParents + "," + taskName + "," + channelName + ']';
    }

    @Override
    public boolean checkCondition(List<TaskOutput> trace) {
        return trace.stream().anyMatch((t) -> nameMatch(t) && t.value().equals(trueVal));
    }

    boolean nameMatch(TaskOutput t) {
        var b1 = t.channel().name().equals(this.channelName);
        var b2 = t.task().getTaskName().equals(this.taskName);
        var b3 = isSuffixOf(otherParents, t.task().getParents());
        return b1 && b2 && b3;
    }
}
