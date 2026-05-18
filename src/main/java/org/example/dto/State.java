package org.example.dto;

import org.example.dto.task.output.TaskOutput;
import org.example.dto.task.output.value.Value;

import java.util.List;
import java.util.Optional;

/** A state represents a list of task outputs (on channels) and the value of local variables (attibutes). */
public interface State extends List<TaskOutput> {

    /** Write a value into a local variable. */
    void write(String name, Value val);

    Optional<Value> getFromMemory(String name);

    default Optional<Value> getFromChannel(String id) {
        Optional<TaskOutput> first = this.stream().filter((to) -> to.channel().name().equals(id)).findFirst();
        return first.map(TaskOutput::value);

    }

}
