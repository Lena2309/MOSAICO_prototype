package org.example.orchestrator;

import jakarta.el.LambdaExpression;
import org.example.agents.CollaborationAgent;
import org.example.dto.*;

import java.util.List;
import java.util.Optional;

public interface Orchestrator {
    CollaborationAgent collaborationAgent();
    WorkflowType getWorkflowType();
    List<TaskOutput> run(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs, Optional<LambdaExpression> endLoopCondition);

    default void executeItem(OrderedMOSAICOExecution item, List<TaskOutput> taskOutputs) {
        if (item instanceof Task task) {
            var necessaryOutputs = taskOutputs.stream()
                    .filter(taskOutput -> task.outputDependencies().contains(taskOutput.task()))
                    .toList();
            taskOutputs.add(task.execute(necessaryOutputs));
        } else if (item instanceof TaskExecutionPlan subPlan) {
            collaborationAgent().runOrchestrator(subPlan.tasks(), subPlan.taskExecutionPlans(), subPlan.workflowType(), taskOutputs, subPlan.endLoopCondition());
        }
    }
}
