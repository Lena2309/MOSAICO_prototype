package org.example.agents.mosaico;

import org.example.dto.ChannelState;
import org.example.dto.task.AgentTask;
import org.example.dto.task.output.TaskOutput;
import org.example.dto.task.output.Channel;

import java.util.List;

public class ConsensusAgent extends MosaicoAgent {
    public ConsensusAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public TaskOutput performTask(AgentTask task, ChannelState dependencies, Channel channel) {
        return null;
    }
}
