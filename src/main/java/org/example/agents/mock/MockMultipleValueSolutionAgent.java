package org.example.agents.mock;

import org.example.dto.task.output.value.MultipleValue;
import org.example.dto.task.output.value.StringValue;
import org.example.dto.task.output.value.Value;

import java.util.List;

public class MockMultipleValueSolutionAgent extends MockSolutionAgent {


    public final MultipleValue OUTPUT = new MultipleValue();

    public MockMultipleValueSolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
        OUTPUT.addValue(new StringValue("blabla1"));
        OUTPUT.addValue(new StringValue("blabla2"));
    }

    @Override
    Value mockOutput() {
        return OUTPUT;
    }

}
