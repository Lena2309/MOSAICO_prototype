package org.example.dto.step;

import org.example.dto.task.AgentTask;
import org.example.dto.task.AssignmentTask;

import java.util.Optional;

public class AssignmentStep extends Step {
    public AssignmentStep(AssignmentTask assignmentTask) {
        super(assignmentTask);
    }

    public AssignmentStep(Optional<Step> nextStep) {
        super(nextStep);
    }

    public AssignmentStep(AgentTask agentTask, Optional<Step> nextStep) {
        super(agentTask, nextStep);
    }
}
