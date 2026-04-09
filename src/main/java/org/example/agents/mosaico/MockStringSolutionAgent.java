package org.example.agents.mosaico;

import org.example.dto.task.AgentTask;
import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.BooleanValue;
import org.example.dto.task.output.Channel;
import org.example.dto.task.output.StringValue;

import java.util.List;

public class MockStringSolutionAgent extends SolutionAgent {
    public MockStringSolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public AgentTaskOutput callLLM(AgentTask task, List<AgentTaskOutput> dependencies, Channel channel) {
        return new AgentTaskOutput(task, channel, new StringValue("bla bla"));
    }
}
