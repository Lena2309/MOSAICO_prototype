package org.example.agents.mock;

import org.example.dto.task.output.value.StringValue;
import org.example.dto.task.output.value.Value;

import java.util.List;

public class MockStringSolutionAgent extends MockSolutionAgent {


    public static final Value OUTPUT = new StringValue("bla bla");

    public MockStringSolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    Value mockOutput() {
        return OUTPUT;
    }

}
