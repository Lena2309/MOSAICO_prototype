package org.example.agents.mockAgent;

import org.example.agents.mosaico.SupervisionAgent;
import org.example.dto.task.output.BooleanValue;

import java.util.List;

/**
 * An agent that always returns true.
 */
public class MockTrueEvaluatorAgent extends SupervisionAgent {
    public MockTrueEvaluatorAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public BooleanValue fakeResult() {
        return new BooleanValue(true);
    }
}
