package org.example.agents;

import java.util.List;

public class SupervisionAgent extends MosaicoAgent{
    public SupervisionAgent(String id, String name, String description,  List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public Value fakeResult() {
        return new BooleanValue(true);
    }
}
