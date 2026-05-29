package org.example.agents.mock;

import org.example.dto.task.output.value.StringValue;
import org.example.dto.task.output.value.Value;

import java.util.List;

public class MockStringSolutionAgent extends MockSolutionAgent {


    public static final String OUTPUT = "bla bla ";

    int cpt=0;

    public MockStringSolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    Value mockOutput() {
        cpt++;
        return new StringValue(OUTPUT + cpt) ;
    }

}
