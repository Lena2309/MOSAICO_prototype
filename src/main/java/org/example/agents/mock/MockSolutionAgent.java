package org.example.agents.mock;

import org.example.agents.mosaico.MosaicoAgent;
import org.example.agents.mosaico.SolutionAgent;
import org.example.dto.task.AgentTask;
import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.Channel;
import org.example.dto.task.output.Value;

import java.util.List;

public abstract class MockSolutionAgent extends SolutionAgent {
    static final String input_prefix = "[MOCK INPUT] ";
    static final String output_prefix = "[MOCK OUTPUT] ";

    public MockSolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    abstract Value mockOutput();

    public void logInputs(AgentTask task, List<AgentTaskOutput> dependencies){
        System.out.println(input_prefix + "Task description= '" + task.getTaskDescription() + "'");
        for (Channel c : task.getInputChannels())
            System.out.println(input_prefix + "Channel " + c.getName() + "= " + MosaicoAgent.readChannel(c, dependencies));
    }

    @Override
    public AgentTaskOutput performTask(AgentTask task, List<AgentTaskOutput> dependencies, Channel channel) {
        logInputs(task, dependencies);
        System.out.println(output_prefix + mockOutput());
        return new AgentTaskOutput(task, channel, mockOutput());
    }

}
