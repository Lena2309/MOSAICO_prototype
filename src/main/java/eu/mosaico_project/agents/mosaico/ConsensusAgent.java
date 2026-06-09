package eu.mosaico_project.agents.mosaico;

import eu.mosaico_project.miol.ChannelState;
import eu.mosaico_project.miol.task.AgentTask;
import eu.mosaico_project.miol.task.output.TaskOutput;
import eu.mosaico_project.miol.task.output.Channel;

import java.security.InvalidParameterException;
import java.util.List;

@Deprecated
public class ConsensusAgent extends MosaicoAgent {
    public ConsensusAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, null, constraints);
    }

    @Override
    public TaskOutput performTask(AgentTask task, ChannelState dependencies, Channel channel) {
        throw new InvalidParameterException("FIXME: Consensus agent not implemented.");
    }
}
