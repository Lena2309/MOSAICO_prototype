package org.example.dto;

import org.example.agents.ConsensusAgent;
import org.example.agents.MosaicoAgent;
import org.example.agents.ReferenceAgent;
import org.example.agents.SupervisionAgent;

import java.util.ArrayList;
import java.util.List;

public class Task implements OrderedMOSAICOExecution {
    private final int executionOrder;
    private final String taskName;
    private final String taskDescription;
    private final List<String> taskOutputsNames;
    private MosaicoAgent bestAgent;
    private List<Task> outputDependencies;

    public Task(int executionOrder, String taskName, String taskDescription, MosaicoAgent bestAgent) {
        this(executionOrder, taskName, taskDescription, new ArrayList<>(), bestAgent, new ArrayList<>());
    }

    public Task(int executionOrder, String taskName, String taskDescription,
                List<String> taskOutputsNames, MosaicoAgent bestAgent, List<Task> outputDependencies) {
        this.executionOrder = executionOrder;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskOutputsNames = taskOutputsNames;
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

    public TaskOutput execute(List<TaskOutput> dependenciesOutputs) {
        switch (bestAgent) {
            case null -> {
                // choper un agent
                return new TaskOutput(this, this.taskDescription);
                // skip ou exception
            }
            case ReferenceAgent referenceAgent -> {
            }
            case SupervisionAgent supervisionAgent -> {
            }
            case ConsensusAgent consensusAgent -> {
            }
            default -> {
                return new TaskOutput(this, this.taskDescription);
            }
        }
        System.out.println("Task " + getTaskName() + " executed successfully.");
        return new TaskOutput(this, this.taskDescription);
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

}
