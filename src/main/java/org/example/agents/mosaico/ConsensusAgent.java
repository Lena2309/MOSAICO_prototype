package org.example.agents.mosaico;

import org.example.dto.task.output.BooleanValue;
import org.example.dto.task.output.Value;

import java.util.List;

public class ConsensusAgent extends MosaicoAgent {
    public ConsensusAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public Value fakeResult() {
        return new BooleanValue(true);
    }
}
