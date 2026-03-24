package org.example.agents;

import java.util.List;

public class FallbackAgent extends SolutionAgent {
    public FallbackAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public StringValue fakeResult() {
        return new StringValue("fallback bla bla");
    }
}
