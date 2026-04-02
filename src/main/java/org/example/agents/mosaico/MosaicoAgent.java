package org.example.agents.mosaico;

import org.example.agents.MosaicoAgentType;
import org.example.dto.task.AgentTask;
import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.Channel;

import java.util.List;

public abstract class MosaicoAgent {
    private final MosaicoAgentType agentType;
    private final String id;
    private final String licence;
    private final List<String> constraints;
    private String name;
    private List<String> skills;

    public MosaicoAgent(String id, String licence, List<String> constraints) {
        this(null, id, null, licence, constraints);
    }

    public MosaicoAgent(String id, String name, String licence, List<String> constraints) {
        this(null, id, name, licence, constraints);
    }

    public MosaicoAgent(MosaicoAgentType agentType, String id, String name, String licence, List<String> constraints) {
        this.agentType = agentType;
        this.id = id;
        this.name = name;
        this.licence = licence;
        this.constraints = constraints;
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

    public String getLicence() {
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

    @Deprecated
    abstract public AgentTaskOutput callLLM(AgentTask task, List<AgentTaskOutput> dependencies, Channel channel);
}
