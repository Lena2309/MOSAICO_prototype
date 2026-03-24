package org.example.agents;

import java.util.List;

public class GeneratorAgent extends SolutionAgent {
    public GeneratorAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public StringValue fakeResult(){
        return new StringValue("generated bla bla");
    }
}