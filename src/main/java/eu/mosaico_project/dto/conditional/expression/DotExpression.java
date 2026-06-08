package eu.mosaico_project.dto.conditional.expression;

import eu.mosaico_project.dto.AttributeState;
import eu.mosaico_project.dto.ChannelState;
import eu.mosaico_project.dto.task.output.TaskOutput;
import eu.mosaico_project.dto.task.output.value.Value;

import java.security.InvalidParameterException;
import java.util.List;

public class DotExpression implements Expression {
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
    public Value eval(ChannelState trace, AttributeState memory) {
        var results = trace.stream().filter(this::nameMatch).toList();
        if (!results.isEmpty())
            return results.getLast().value() ;
        else
            // FIXME : search also in memory
            throw new InvalidParameterException("Not found: " + this.channelName);
    }

    boolean nameMatch(TaskOutput t) {
        // FIXME : check also into attribute memory ?
        var b1 = t.channel().name().equals(this.channelName);
        var b2 = t.task().getTaskName().equals(this.taskName);
        var b3 = isSuffixOf(otherParents, t.task().getParents());
        return b1 && b2 && b3;
    }
}
