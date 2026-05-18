package org.example.dto;

import org.example.dto.task.output.TaskOutput;
import org.example.dto.task.output.value.Value;

import java.util.List;

/** A state represents a list of task outputs (on channels) and the value of local variables (attibutes). */
public interface State extends List<TaskOutput> {

    /** Write a value into a local variable. */
    void write(String name, Value val);
}
