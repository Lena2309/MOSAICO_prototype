package eu.mosaico_project.agents.mock;

import eu.mosaico_project.dto.task.output.value.BooleanValue;
import eu.mosaico_project.dto.task.output.value.Value;

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
