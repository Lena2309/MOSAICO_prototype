package org.example.dto.task;

import org.example.dto.AttributeState;
import org.example.dto.ChannelState;
import org.example.dto.ChannelStateImpl;
import org.example.dto.task.output.Channel;


import java.util.List;
import java.util.stream.Collectors;

public abstract class Task {
    protected final String taskName;
    protected final String taskDescription;
    protected final List<Channel> outputChannels;
    protected final List<Channel> inputChannels;
    protected final List<Task> inputTaskDependencies;
    protected final List<String> parents;

    public Task(String taskName, String taskDescription,
                List<Channel> taskOutputsNames,
                List<Channel> inputChannels, List<Task> inputTaskDependencies,
                List<String> parents) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.outputChannels = taskOutputsNames;
        this.inputChannels = inputChannels;
        this.inputTaskDependencies = inputTaskDependencies;
        this.parents = parents;
    }

    public abstract ChannelState execute(ChannelState allTaskOutputs, AttributeState memory) ;

    // Getters
    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public List<Channel> getOutputChannels() {
        return outputChannels;
    }

    public List<Channel> getInputChannels() {
        return inputChannels;
    }

    public List<Task> getInputTaskDependencies() {
        return inputTaskDependencies;
    }

    public List<String> getParents() {
        return parents;
    }

    public String toString() {
        String dependenciesStr = (inputTaskDependencies != null && !inputTaskDependencies.isEmpty())
                ? ", dependencies=[" + inputTaskDependencies.stream().map(Task::getTaskName).collect(Collectors.joining(", ")) + "]"
                : "";

        return String.format("Task{name='%s', description='%s', parents='%s', dependencies=%s}",
                taskName,
                taskDescription,
                parents,
                dependenciesStr);
    }
}
