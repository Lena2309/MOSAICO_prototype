package eu.mosaico_project.agents.mosaico;

import eu.mosaico_project.dto.ChannelState;
import eu.mosaico_project.dto.task.AgentTask;
import eu.mosaico_project.dto.task.output.TaskOutput;
import eu.mosaico_project.dto.task.output.Channel;

import java.security.InvalidParameterException;
import java.util.List;

@Deprecated
public class ConsensusAgent extends MosaicoAgent {
    public ConsensusAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public TaskOutput performTask(AgentTask task, ChannelState dependencies, Channel channel) {
        throw new InvalidParameterException("FIXME: Consensus agent not implemented.");
    }
}
