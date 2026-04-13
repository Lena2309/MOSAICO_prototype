package org.example.dto.task;

import org.example.agents.FallbackAgent;
import org.example.agents.mosaico.ConsensusAgent;
import org.example.agents.mosaico.MosaicoAgent;
import org.example.agents.mosaico.ReferenceAgent;
import org.example.agents.mosaico.SupervisionAgent;
import org.example.dto.task.output.Channel;
import org.example.dto.task.output.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AgentTask {
    private final String taskName;
    private final String taskDescription;
    private final List<Channel> outputChannels;
    private final List<Channel> inputChannels;
    private final List<AgentTask> inputTaskDependencies;
    private MosaicoAgent bestAgent;

    public AgentTask(String taskName, String taskDescription, MosaicoAgent bestAgent) {
        this(taskName, taskDescription, new ArrayList<>(), bestAgent, new ArrayList<>(), new ArrayList<>());
    }

    public AgentTask(String taskName, String taskDescription,
                     List<Channel> taskOutputsNames, MosaicoAgent bestAgent,
                     List<Channel> inputChannels, List<AgentTask> inputTaskDependencies) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.outputChannels = taskOutputsNames;
        this.bestAgent = bestAgent;
        this.inputChannels = inputChannels;
        this.inputTaskDependencies = inputTaskDependencies;
    }

    // TODO: implement repo talk
    public static MosaicoAgent findBestAgentForTask(List<MosaicoAgent> agentPool, List<String> taskKeywords, String taskDescription, MosaicoAgent agentToAvoid) {
        if (agentToAvoid != null) {
            return agentToAvoid;
        }
        return null;
    }

    public List<AgentTaskOutput> execute(List<AgentTaskOutput> allTaskOutputs) {
        var latestDependenciesOutputs = new ArrayList<AgentTaskOutput>();

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
            for (var channel: this.inputChannels){
                switch (bestAgent){
                    case ReferenceAgent referenceAgent -> {
                        // Note: We use reference agent in-channel only if it has no out-channel
                        referenceAgent.showToUser(this.taskDescription);
                        // FIXME: print the content of the input channel in addition to the description
                    }
                    default -> {}
                }
            }
            return List.of();
        } else {
            var outputList = new ArrayList<AgentTaskOutput>();
            for (var channel : this.outputChannels) {
                switch (bestAgent) {
                    case null -> {
                        System.out.print("[WARNING] Using a fallback agent.");
                        outputList.add(new FallbackAgent().callLLM(this, allTaskOutputs, channel));
                    }
                    case ReferenceAgent referenceAgent -> {
                        var res = referenceAgent.askToUser(this.taskDescription);
                        AgentTaskOutput out = new AgentTaskOutput(this, channel, new StringValue(res));
                        outputList.add(out);
                    }
                    case SupervisionAgent supervisionAgent -> {
                        // pass
                    }
                    case ConsensusAgent consensusAgent -> {
                        // pass
                    }
                    default -> outputList.add(bestAgent.callLLM(this, allTaskOutputs, channel));
                }
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


    public String toString() {
        String dependenciesStr = (inputTaskDependencies != null && !inputTaskDependencies.isEmpty())
                ? ", dependencies=[" + inputTaskDependencies.stream().map(AgentTask::getTaskName).collect(Collectors.joining(", ")) + "]"
                : "";

        return String.format("AgentTask{name='%s', description='%s', agent='%s'%s}",
                taskName,
                taskDescription,
                bestAgent != null ? bestAgent.getName() : "None",
                dependenciesStr);
    }
}
