package eu.mosaico_project.dto;

import eu.mosaico_project.dto.task.output.TaskOutput;
import eu.mosaico_project.dto.task.output.value.Value;

import java.util.List;
import java.util.Optional;

/** A Channel State represents a list of task outputs (on channels). */
public interface ChannelState extends List<TaskOutput> {

    default Optional<Value> getFromChannel(String id) {
        List<TaskOutput> candidates =  this.stream().filter((to) -> to.channel().name().equals(id)).toList();
        // See also DotExpression.nameMatch for more complex filtering.
        if (candidates.isEmpty())
            return Optional.empty();
        else
            // return the last in the list (most recent value).
            return Optional.of(candidates.getLast().value());
    }

}
