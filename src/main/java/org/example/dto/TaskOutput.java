package org.example.dto;

import org.example.agents.Channel;
import org.example.agents.Value;

/** Example: the task "checkCorrectness" writes the boolean value "true" on its output channel named "correct". */
public record TaskOutput(Task task, Channel channel, Value value) {
    public String toString(){
        return task.toString() + "." + channel + " = " + value ;
    }
}
