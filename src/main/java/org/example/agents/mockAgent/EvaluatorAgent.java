package org.example.agents.mockAgent;

import org.example.agents.SupervisionAgent;
import org.example.dto.output.BooleanValue;

import java.util.List;

public class EvaluatorAgent extends SupervisionAgent {
    public EvaluatorAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public BooleanValue fakeResult() {
        return new BooleanValue(true);
    }
}
