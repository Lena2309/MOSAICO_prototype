package org.example.dto;

import org.example.dto.task.output.TaskOutput;
import org.example.dto.task.output.value.Value;

import java.util.List;
import java.util.Optional;

/** A Channel State represents a list of task outputs (on channels). */
public interface ChannelState extends List<TaskOutput> {

    default Optional<Value> getFromChannel(String id) {
        Optional<TaskOutput> first = this.stream().filter((to) -> to.channel().name().equals(id)).findFirst();
        return first.map(TaskOutput::value);
    }

}
