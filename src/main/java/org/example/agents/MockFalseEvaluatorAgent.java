package org.example.agents;

import java.util.List;

/** An agent that always returns false. */
public class MockFalseEvaluatorAgent extends SupervisionAgent {
    public MockFalseEvaluatorAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public BooleanValue fakeResult(){
        return new BooleanValue(false);
    }
}
