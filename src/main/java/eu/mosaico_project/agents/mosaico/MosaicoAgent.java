package eu.mosaico_project.agents.mosaico;

import eu.mosaico_project.agents.MosaicoAgentType;
import eu.mosaico_project.dto.ChannelState;
import eu.mosaico_project.dto.task.AgentTask;
import eu.mosaico_project.dto.task.output.TaskOutput;
import eu.mosaico_project.dto.task.output.Channel;
import eu.mosaico_project.dto.task.output.value.Value;

import java.util.ArrayList;
import java.util.List;

public abstract class MosaicoAgent {

    public record Licence (String info){} ;

    private final MosaicoAgentType agentType;
    private final String id;
    private final Licence licence;
    private final List<String> constraints;
    private String name;
    private List<String> skills;

    @Deprecated
    public MosaicoAgent(String id, Licence licence, List<String> constraints) {
        this(null, id, null, licence, constraints);
    }

    public MosaicoAgent(String id, String name, Licence licence, List<String> constraints) {
        this(null, id, name, licence, constraints);
    }

    // FIXME : always called with first parameter = null
    public MosaicoAgent(MosaicoAgentType agentType, String id, String name, Licence licence, List<String> constraints) {
        this.agentType = agentType;
        this.id = id;
        this.name = name;
        this.licence = licence;
        this.constraints = constraints;
        this.skills = new ArrayList<>();
    }

    /**
     * Utility to read the value of a channel in a trace.
     */
    public static Value readChannel(Channel c, List<TaskOutput> trace) {
        var res = trace.stream().filter((TaskOutput out) -> out.channel().equals(c)).findAny();
        if (res.isPresent())
            return res.get().value();
        else
            return null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Licence getLicence() {
        return licence;
    }

    public List<String> getConstraints() {
        return constraints;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    abstract public TaskOutput performTask(AgentTask task, ChannelState dependencies, Channel channel);
}
