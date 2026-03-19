package org.example.agents;

import java.util.List;

public abstract class MosaicoAgent {
    private final String id;
    private String name;
    private final String licence;
    private final  List<String> constraints;
    private List<String> skills;

    public MosaicoAgent(String id, String licence, List<String> constraints) {
        this(id, null, licence, constraints);
    }

    public MosaicoAgent(String id, String name, String licence, List<String> constraints) {
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

    public  List<String> getConstraints() {
        return constraints;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    @Deprecated
    abstract public Value fakeResult();
}
