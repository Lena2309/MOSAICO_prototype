package org.example.agents.mosaico;

import org.example.dto.task.output.StringValue;
import org.example.dto.task.output.Value;

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
