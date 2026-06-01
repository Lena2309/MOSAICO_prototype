package eu.mosaico_project.agents;

import java.util.List;

public class MosaicoAgentType {
    private final String id;
    private final String licence;
    private final List<String> constraints;
    private List<String> skills;

    public MosaicoAgentType(String id, String licence, List<String> constraints, List<String> skills) {
        this.id = id;
        this.licence = licence;
        this.constraints = constraints;
        this.skills = skills;
    }

    public String getId() {
        return id;
    }

    public String getLicence() {
        return licence;
    }
}
