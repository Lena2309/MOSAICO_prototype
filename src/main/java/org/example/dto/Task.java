package org.example.dto;

import org.example.agents.MosaicoAgent;
import java.util.List;

public record Task(int executionOrder, String taskDescription, MosaicoAgent bestAgent, List<Task> outputDependencies) implements OrderedMOSAICOExecution {
    public TaskOutput execute(List<TaskOutput> dependenciesOutputs) {
        return new TaskOutput(this, "");
    }
}
