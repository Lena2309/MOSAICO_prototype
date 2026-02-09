package org.example.orchestrator;

import org.example.agents.CollaborationAgent;
import org.example.dto.*;

import java.util.List;

public interface Orchestrator {
    CollaborationAgent collaborationAgent();
    WorkflowType getWorkflowType();
    List<TaskOutput> run(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs);

    default void executeItem(CollaborationAgent collaborationAgent, OrderedMOSAICOExecution item, List<TaskOutput> taskOutputs) {
        if (item instanceof Task task) {
            var necessaryOutputs = taskOutputs.stream()
                    .filter(taskOutput -> task.outputDependencies().contains(taskOutput.task()))
                    .toList();
            taskOutputs.add(task.execute(necessaryOutputs));
        } else if (item instanceof TaskExecutionPlan subPlan) {
            taskOutputs.addAll(collaborationAgent.runOrchestrator(subPlan.tasks(), subPlan.taskExecutionPlans(), subPlan.workflowType(), taskOutputs));
        }
    }
}
