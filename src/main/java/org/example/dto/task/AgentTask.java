package org.example.dto.task;

import org.example.agents.mosaico.*;
import org.example.dto.task.output.BooleanValue;
import org.example.dto.task.output.Channel;
import org.example.dto.task.output.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgentTask {
    private final String taskName;
    private final String taskDescription;
    private final List<Channel> outputChannels;
    private final List<AgentTask> outputDependencies;
    private MosaicoAgent bestAgent;

    public AgentTask(String taskName, String taskDescription, MosaicoAgent bestAgent) {
        this(taskName, taskDescription, new ArrayList<>(), bestAgent, new ArrayList<>());
    }

    public AgentTask(String taskName, String taskDescription,
                     List<Channel> taskOutputsNames, MosaicoAgent bestAgent, List<AgentTask> outputDependencies) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.outputChannels = taskOutputsNames;
        this.bestAgent = bestAgent;
        this.outputDependencies = outputDependencies;
    }

    // TODO: implement repo talk
    public static MosaicoAgent findBestAgentForTask(List<MosaicoAgent> agentPool, List<String> taskKeywords, String taskDescription, MosaicoAgent agentToAvoid) {
        if (agentToAvoid != null) {
            return agentToAvoid;
        }
        return null;
    }

    public Optional<AgentTaskOutput> execute(List<AgentTaskOutput> dependenciesOutputs) {
        if (this.getDefaultOutputChannel().isEmpty()) {
            System.out.println("[WARNING] No output channel for this task.");
            return Optional.empty();
        } else {
            switch (bestAgent) {
                case null -> {
                    // choper un agent
                    System.out.print("[WARNING] Using a fallback agent.");
                    return Optional.of(new AgentTaskOutput(this, this.getDefaultOutputChannel().get(), new StringValue(this.taskDescription)));
                    // skip ou exception
                }
                case ReferenceAgent referenceAgent -> {
                    // pass
                }
                case SupervisionAgent supervisionAgent -> {
                    // pass
                }
                case ConsensusAgent consensusAgent -> {
                    // pass
                }
                case SolutionAgent a -> {
                    // pass
                }

                // FIXME
                default -> {
                    return Optional.of(new AgentTaskOutput(this, this.getDefaultOutputChannel().get(), new BooleanValue(true)));// bestAgent.fakeResult()));
                }
            }
            System.out.println("Task " + getTaskName() + " executed successfully.");
            return Optional.of(new AgentTaskOutput(this, this.getDefaultOutputChannel().get(), new BooleanValue(true)));
        }
    }

    // Getters

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public MosaicoAgent getBestAgent() {
        return bestAgent;
    }

    public List<AgentTask> getOutputDependencies() {
        return outputDependencies;
    }

    // Add Method
    public void addOutputDependency(AgentTask task) {
        this.outputDependencies.add(task);
    }

    /**
     * Returns a random output channel of this task.
     * You should never use this and always use the relevant output channel instead.
     */
    @Deprecated
    Optional<Channel> getDefaultOutputChannel() {
        if (this.outputChannels.isEmpty())
            return Optional.empty();
        else return Optional.of(outputChannels.getFirst());
    }


    public String toString() {
        return this.taskName;
    }
}
