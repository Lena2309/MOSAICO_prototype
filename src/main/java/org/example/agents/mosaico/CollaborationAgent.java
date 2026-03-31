package org.example.agents.mosaico;

import org.example.dto.step.Step;
import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.StringValue;
import org.example.dto.task.output.Value;

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
        System.out.println("--- Starting CollaborationAgent Orchestration ---");

        List<AgentTaskOutput> taskOutputs = new ArrayList<>();
        Step currentStep = firstStep;

        // Traverse the linked graph sequentially
        while (currentStep != null) {

            // Polymorphic execution:
            // If it's a ParallelStep, it will spawn threads for its body.
            // If it's a LoopStep, it will loop over its body.
            // If it's a standard Step, it will just execute the AgentTask.
            currentStep.execute(taskOutputs);

            // Move to the next linked step in the main sequence
            currentStep = currentStep.getNextStep().orElse(null);
        }

        System.out.println("--- Finished CollaborationAgent Orchestration ---");
        return taskOutputs.toString();
    }

    @Override
    public Value fakeResult() {
        return new StringValue("my result is bla");
    }
}
