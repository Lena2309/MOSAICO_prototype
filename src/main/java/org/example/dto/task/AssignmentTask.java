package org.example.dto.task;

import org.example.dto.conditional.expression.Expression;
import org.example.dto.task.output.Channel;
import org.example.dto.task.output.TaskOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentTask extends Task {
    private Expression assignmentExpression; // FIXME

    public AssignmentTask(String taskName, String taskDescription) {
        this(taskName, taskDescription, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null);
    }

    public AssignmentTask(String taskName, String taskDescription,
                          List<Channel> taskOutputsNames,
                          List<Channel> inputChannels, List<Task> inputTaskDependencies,
                          List<String> parents, Expression assignmentExpression) {
        super(taskName, taskDescription, taskOutputsNames, inputChannels, inputTaskDependencies, parents);
        this.assignmentExpression = assignmentExpression;
    }

    public List<TaskOutput> execute(List<TaskOutput> allTaskOutputs) {
        // FIXME
        var latestDependenciesOutputs = new ArrayList<TaskOutput>();

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
            return List.of();
        } else {
            var outputList = new ArrayList<TaskOutput>();
            for (var channel : this.outputChannels) {
                // System.out.println("Task " + getTaskName() + ", with channel " + channel.getName() + ", executed successfully.");
            }
            return outputList;
        }
    }

    @Override
    public String toString() {
        String dependenciesStr = (inputTaskDependencies != null && !inputTaskDependencies.isEmpty())
                ? ", dependencies=[" + inputTaskDependencies.stream().map(Task::getTaskName).collect(Collectors.joining(", ")) + "]"
                : "";

        return String.format("AssignmentTask{name='%s'%s}",
                taskName,
                dependenciesStr);
    }
}
