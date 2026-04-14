package org.example.agents.mosaico;

import org.example.dto.step.Step;
import org.example.dto.task.AgentTask;
import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.Channel;

import java.util.ArrayList;
import java.util.List;

public class CollaborationAgent extends MosaicoAgent {
    private CollaborationAgent managerAgent;
    private List<CollaborationAgent> collaborationAgentPool;
    private List<MosaicoAgent> agentPool = new ArrayList<>();

    public CollaborationAgent() {
        this(null, null);
        this.agentPool.add(new ReferenceAgent());
    }

    public CollaborationAgent(CollaborationAgent managerAgent) {
        this(managerAgent, null);
    }

    public CollaborationAgent(CollaborationAgent managerAgent, List<MosaicoAgent> agentPool) {
        super(null, null, null, null);
        this.managerAgent = managerAgent;
        if (agentPool != null) {
            this.agentPool.addAll(agentPool);
        }
    }

    // ----------------------------------------------------------

    public CollaborationAgent getManagerAgent() {
        return managerAgent;
    }

    public List<MosaicoAgent> getAgentPool() {
        return agentPool;
    }

    // ----------------------------------------------------------
    // New Execution Engine
    // ----------------------------------------------------------

    /**
     * Executes the workflow starting from the provided first Step.
     * Iterates through the linked list of steps.
     * * @param firstStep The head of the execution chain.
     *
     * @return String representation of the outputs.
     */
    public String run(Step firstStep) {
        System.out.println("--- Starting Collaboration Agent Orchestration ---");

        List<AgentTaskOutput> allTaskOutputs = new ArrayList<>();
        Step currentStep = firstStep;

        // Traverse the linked graph sequentially
        while (currentStep != null) {
            currentStep.execute(allTaskOutputs);
            currentStep = currentStep.getNextStep().orElse(null);
        }

        System.out.println("--- Finished Collaboration Agent Orchestration ---");
        return allTaskOutputs.toString();
    }

    @Override
    public AgentTaskOutput callLLM(AgentTask task, List<AgentTaskOutput> dependencies, Channel channel) {
        return null;
    }
}
