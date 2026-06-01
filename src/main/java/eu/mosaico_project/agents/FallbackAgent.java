package eu.mosaico_project.agents;

import eu.mosaico_project.agents.mosaico.SolutionAgent;

import java.util.List;
import java.util.UUID;

public class FallbackAgent extends SolutionAgent {
    public FallbackAgent() {
        this(UUID.randomUUID().toString(), "FallbackAgent", "", List.of());
    }

    public FallbackAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }
}
