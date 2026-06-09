package eu.mosaico_project.agents.mock;

import eu.mosaico_project.agents.mosaico.MosaicoAgent;
import eu.mosaico_project.agents.mosaico.SolutionAgent;
import eu.mosaico_project.miol.ChannelState;
import eu.mosaico_project.miol.task.AgentTask;
import eu.mosaico_project.miol.task.output.Channel;
import eu.mosaico_project.miol.task.output.TaskOutput;
import eu.mosaico_project.miol.task.output.value.Value;

import java.util.List;

public abstract class MockSolutionAgent extends SolutionAgent {
    static final String input_prefix = "[MOCK INPUT] ";
    static final String output_prefix = "[MOCK OUTPUT] ";

    public MockSolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    abstract Value mockOutput();

    public void logInputs(AgentTask task, List<TaskOutput> dependencies) {
        System.out.println(input_prefix + "Task description= '" + task.getTaskDescription() + "'");
        for (Channel c : task.getInputChannels())
            System.out.println(input_prefix + "Channel " + c.name() + "= " + MosaicoAgent.readChannel(c, dependencies));
    }

    @Override
    public TaskOutput performTask(AgentTask task, ChannelState dependencies, Channel channel) {
        logInputs(task, dependencies);
        Value output = mockOutput();
        System.out.println(output_prefix + output);
        return new TaskOutput(task, channel, output);
    }

}
