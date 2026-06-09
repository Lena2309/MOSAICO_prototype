package eu.mosaico_project.agents.mock;

import eu.mosaico_project.miol.task.output.value.StringValue;
import eu.mosaico_project.miol.task.output.value.Value;

import java.util.List;

public class MockStringSolutionAgent extends MockSolutionAgent {


    public static final String OUTPUT = "bla bla ";

    int cpt = 0;

    public MockStringSolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    Value mockOutput() {
        cpt++;
        return new StringValue(OUTPUT + cpt);
    }

}
