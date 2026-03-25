package org.example.agents;

import org.example.dto.output.StringValue;
import org.example.dto.output.Value;

import java.util.List;

public class SolutionAgent extends MosaicoAgent {
    public SolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public Value fakeResult() {
        return new StringValue("solution bla bla");
    }
}
