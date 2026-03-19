package org.example.agents;

import java.util.List;
import java.util.UUID;

public class ReferenceAgent extends MosaicoAgent {
    public ReferenceAgent() {
        super(String.valueOf(UUID.randomUUID()), "Reference Agent", "An agent that directly interacts with the end-user.", null);
    }

    public ReferenceAgent(String id, String name, String description,  List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public Value fakeResult() {
        return new StringValue("hello");
    }
}
