package org.example.agents.mosaico;

import org.example.dto.State;
import org.example.dto.task.AgentTask;
import org.example.dto.task.output.TaskOutput;
import org.example.dto.task.output.Channel;

import java.util.List;

public class SupervisionAgent extends MosaicoAgent {
    public SupervisionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public TaskOutput performTask(AgentTask task, State dependencies, Channel channel) {
        return null;
    }
}
