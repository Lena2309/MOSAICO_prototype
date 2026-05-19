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

    public ChannelState execute(ChannelState allTaskOutputs, AttributeState memory) {
        var latestDependenciesOutputs = new ChannelStateImpl();

        // Iterate backwards to process the most recent outputs first
        for (int i = allTaskOutputs.size() - 1; i >= 0; i--) {
            var output = allTaskOutputs.get(i);

            // 1. Ignore if current task is not dependent to output task
            if (!this.inputTaskDependencies.contains(output.task())) {
                continue;
            }

            // 2. Ignore if it's not a used input channel
            if (!this.inputChannels.contains(output.channel())) {
                continue;
            }

            // 3. Check if we already collected a newer output for this exact Task + Channel
            boolean alreadyCollected = latestDependenciesOutputs.stream()
                    .anyMatch(existing -> existing.task().equals(output.task())
                            && existing.channel().equals(output.channel()));

            // 4. If we haven't seen it yet, add it
            if (!alreadyCollected) {
                latestDependenciesOutputs.add(output);
            }
        }

        if (this.outputChannels.isEmpty()) {
            System.out.println("    [WARNING] No output for this task.");
            if (this.inputChannels.isEmpty())
                System.out.println("    [WARNING] No input for this task.");
            for (var channel : this.inputChannels) {

            }
            return new ChannelStateImpl();
        } else {
            var outputList = new ChannelStateImpl();
            for (var channel : this.outputChannels) {

                // System.out.println("Task " + getTaskName() + ", with channel " + channel.getName() + ", executed successfully.");
            }
            return outputList;
        }
    }

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
