package eu.mosaico_project.miol.task;

import eu.mosaico_project.agents.FallbackAgent;
import eu.mosaico_project.agents.mosaico.ConsensusAgent;
import eu.mosaico_project.agents.mosaico.MosaicoAgent;
import eu.mosaico_project.agents.mosaico.ReferenceAgent;
import eu.mosaico_project.agents.mosaico.SupervisionAgent;
import eu.mosaico_project.miol.AttributeState;
import eu.mosaico_project.miol.ChannelState;
import eu.mosaico_project.miol.ChannelStateImpl;
import eu.mosaico_project.miol.task.output.Channel;
import eu.mosaico_project.miol.task.output.TaskOutput;

import java.util.*;
import java.util.stream.Collectors;

public class AgentTask extends Task {
    private final Map<String, String> otherProperties;
    private MosaicoAgent bestAgent;

    @Deprecated
    public AgentTask(String taskName, String taskDescription, MosaicoAgent bestAgent) {
        this(taskName, taskDescription, new ArrayList<>(), bestAgent, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    public AgentTask(String taskName, String taskDescription,
                     List<Channel> taskOutputsNames, MosaicoAgent bestAgent,
                     List<Channel> inputChannels, List<Task> inputTaskDependencies,
                     List<String> parents, Map<String, String> otherProperties) {
        super(taskName, taskDescription, taskOutputsNames, inputChannels, inputTaskDependencies, parents);
        this.bestAgent = bestAgent;
        this.otherProperties = otherProperties;
    }

    // TODO: implement repo talk
    public static MosaicoAgent findBestAgentForTask(List<MosaicoAgent> agentPool, List<String> taskKeywords, String taskDescription, MosaicoAgent agentToAvoid) {
        if (agentToAvoid != null) {
            return agentToAvoid;
        }
        return null;
    }

    public Optional<String> getOtherProperty(String key) {
        if (this.otherProperties.containsKey(key))
            return Optional.of(this.otherProperties.get(key));
        else
            return Optional.empty();
    }

    @Override
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
            System.out.println("    [WARNING] No output for this task: " + this.taskName);
            if (this.inputChannels.isEmpty())
                System.out.println("    [WARNING] No input for this task: " + this.taskName);
            for (var channel : this.inputChannels) {
                switch (bestAgent) {
                    case ReferenceAgent referenceAgent -> {
                        // Note: We use reference agent in-channel only if it has no out-channel
                        referenceAgent.showToUser(this.taskDescription);
                        var res = MosaicoAgent.readChannel(channel, allTaskOutputs);
                        if (res != null)
                            referenceAgent.showToUser(res + "(" + channel.toString() + ")");
                        else
                            System.out.println("[ERROR][REFERENCE AGENT] No value for this channel: " + channel.name());
                    }
                    default -> {
                    }
                }
            }
            return new ChannelStateImpl();
        } else {
            var outputList = new ChannelStateImpl();
            for (var channel : this.outputChannels) {
                switch (bestAgent) {
                    case null -> {
                        System.out.print("[WARNING] Using a fallback agent.");
                        outputList.add(new FallbackAgent().performTask(this, allTaskOutputs, channel));
                    }
                    case ReferenceAgent referenceAgent -> {
                        var res = referenceAgent.askToUser(this.taskDescription, channel);
                        TaskOutput out = new TaskOutput(this, channel, res);
                        outputList.add(out);
                    }
                    case SupervisionAgent supervisionAgent -> {
                        // pass
                    }
                    case ConsensusAgent consensusAgent -> {
                        // pass
                    }
                    default -> outputList.add(bestAgent.performTask(this, allTaskOutputs, channel));
                }
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

        return String.format("AgentTask{name='%s', description='%s', agent='%s'%s}",
                taskName,
                taskDescription,
                bestAgent != null ? bestAgent.getName() : "None",
                dependenciesStr);
    }
}
