package org.example.dto.task;


import org.example.dto.task.output.Channel;
import org.example.dto.task.output.Value;

/**
 * Example: the task "checkCorrectness" writes the boolean value "true" on its output channel named "correct".
 */
public record AgentTaskOutput(AgentTask task, Channel channel, Value value) {
    @Override
    public String toString() {
        return "\n" + task.getTaskName() + "." + channel + " = " + value;
    }
}
