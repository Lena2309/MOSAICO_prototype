package org.example.agents.mock;

import org.example.agents.mosaico.SolutionAgent;
import org.example.dto.task.AgentTask;
import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.BooleanValue;
import org.example.dto.task.output.Channel;

import java.util.List;

public class MockFalseSolutionAgent extends SolutionAgent {
    public MockFalseSolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public AgentTaskOutput callLLM(AgentTask task, List<AgentTaskOutput> dependencies, Channel channel) {
        return new AgentTaskOutput(task, channel, new BooleanValue(false));
    }
}
