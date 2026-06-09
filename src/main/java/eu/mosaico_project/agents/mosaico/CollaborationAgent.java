package eu.mosaico_project.agents.mosaico;

import eu.mosaico_project.dto.AttributeState;
import eu.mosaico_project.dto.AttributeStateImpl;
import eu.mosaico_project.dto.ChannelState;
import eu.mosaico_project.dto.ChannelStateImpl;
import eu.mosaico_project.dto.step.Step;
import eu.mosaico_project.dto.task.AgentTask;
import eu.mosaico_project.dto.task.output.TaskOutput;
import eu.mosaico_project.dto.task.output.Channel;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class CollaborationAgent extends MosaicoAgent {
    private final CollaborationAgent managerAgent;
    private List<MosaicoAgent> agentPool = new ArrayList<>();

    public CollaborationAgent() {
        this(null, null);
        this.agentPool.add(new ReferenceAgent());
    }


    // FIXME : the two parameters are always null
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

        ChannelState allTaskOutputs = new ChannelStateImpl();
        AttributeState mem = new AttributeStateImpl() ;

        Step currentStep = firstStep;

        // Traverse the linked graph sequentially
        while (currentStep != null) {
            currentStep.execute(allTaskOutputs, mem);
            currentStep = currentStep.getNextStep().orElse(null);
        }

        System.out.println("--- Finished Collaboration Agent Orchestration ---");
        return allTaskOutputs.toString();
    }

    @Override
    public TaskOutput performTask(AgentTask task, ChannelState dependencies, Channel channel) {
        throw new InvalidParameterException("Collaboration Agents do not perform tasks.");
    }
}
