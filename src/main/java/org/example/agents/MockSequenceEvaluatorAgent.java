package org.example.agents;

import java.util.List;

/** An agent that returns true or false following a predefined sequence. */
public class MockSequenceEvaluatorAgent extends SupervisionAgent {
    int cpt;

    public MockSequenceEvaluatorAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
        this.cpt = 0;
    }

    @Override
    public BooleanValue fakeResult(){
        cpt++ ;
        return new BooleanValue(cpt%3 == 0);
    }
}
