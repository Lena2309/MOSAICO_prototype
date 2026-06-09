package eu.mosaico_project.miol.task.output;


import eu.mosaico_project.miol.task.Task;
import eu.mosaico_project.miol.task.output.value.Value;

/**
 * Example: the task "checkCorrectness" writes the boolean value "true" on its output channel named "correct".
 */
public record TaskOutput(Task task, Channel channel, Value value) {
    @Override
    public String toString() {
        return "\n" + task.getTaskName() + "." + channel + " = " + value;
    }
}
