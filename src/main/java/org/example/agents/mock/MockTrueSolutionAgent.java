package org.example.agents.mock;

import org.example.dto.task.output.value.BooleanValue;
import org.example.dto.task.output.value.Value;

import java.util.List;

public class MockTrueSolutionAgent extends MockSolutionAgent {

    static final Value OUTPUT = new BooleanValue(true);

    public MockTrueSolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    Value mockOutput() {
        return OUTPUT;
    }

}
