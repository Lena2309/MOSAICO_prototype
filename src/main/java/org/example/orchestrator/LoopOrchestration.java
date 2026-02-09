package org.example.orchestrator;

import jakarta.el.LambdaExpression;
import org.example.agents.CollaborationAgent;
import org.example.dto.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record LoopOrchestration(CollaborationAgent collaborationAgent) implements Orchestrator {
    @Override
    public WorkflowType getWorkflowType() {
        return WorkflowType.LOOP;
    }

    @Override
    public List<TaskOutput> run(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, List<TaskOutput> taskOutputs, Optional<LambdaExpression> endLoopCondition) {
        // TODO: implement

        //if (endLoopCondition.isPresent()) {

            //while (endLoopCondition.get() != true) {


        var executionQueue = Stream.concat(tasks.stream(), taskExecutionPlans.stream())
                .sorted(Comparator.comparingInt(OrderedMOSAICOExecution::executionOrder))
                .toList();


        for (OrderedMOSAICOExecution executable : executionQueue) {
            executeItem(executable, taskOutputs);
        }
            //}
        //}

        return taskOutputs;
    }
}
