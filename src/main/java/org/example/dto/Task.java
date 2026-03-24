package org.example.dto;

import org.example.agents.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Task implements OrderedMOSAICOExecution {
    private final int executionOrder;
    private final String taskName;
    private final String taskDescription;
    private final List<Channel> outputChannels;
    private MosaicoAgent bestAgent;
    private List<Task> outputDependencies;

    public Task(int executionOrder, String taskName, String taskDescription, MosaicoAgent bestAgent) {
        this(executionOrder, taskName, taskDescription, new ArrayList<>(), bestAgent, new ArrayList<>());
    }

    public Task(int executionOrder, String taskName, String taskDescription,
                List<Channel> taskOutputsNames, MosaicoAgent bestAgent, List<Task> outputDependencies) {
        this.executionOrder = executionOrder;
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

    public Optional<TaskOutput> execute(List<TaskOutput> dependenciesOutputs) {
        if (this.getDefaultOutputChannel().isEmpty()){
            System.out.println("[WARNING] No output channel for this task.");
            return Optional.empty();
            }
        else {
            switch (bestAgent) {
                case null -> {
                    // choper un agent
                    System.out.print("[WARNING] Using a fallback agent.");
                    return Optional.of(new TaskOutput(this, this.getDefaultOutputChannel().get(), new StringValue(this.taskDescription)));
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

                default -> {
                    return Optional.of(new TaskOutput(this, this.getDefaultOutputChannel().get(), bestAgent.fakeResult()));
                }
            }
            System.out.println("Task " + getTaskName() + " executed successfully.");
            return Optional.of(new TaskOutput(this, this.getDefaultOutputChannel().get(), bestAgent.fakeResult()));
        }
    }

    // Getters
    @Override
    public int getExecutionOrder() {
        return executionOrder;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public MosaicoAgent getBestAgent() {
        return bestAgent;
    }

    public List<Task> getOutputDependencies() {
        return outputDependencies;
    }

    // Setters
    public void setOutputDependencies(List<Task> outputDependencies) {
        this.outputDependencies = outputDependencies;
    }

    // Add Method
    public void addOutputDependency(Task task) {
        this.outputDependencies.add(task);
    }

    /** Returns a random output channel of this task.
     * You should never use this and always use the relevant output channel instead. */
    @Deprecated
    Optional<Channel> getDefaultOutputChannel(){
        if (this.outputChannels.isEmpty())
                return Optional.empty() ;
        else return Optional.of(outputChannels.getFirst());
    }


    public String toString(){
        return this.taskName ;
    }
}
