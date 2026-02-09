package org.example.agents;

import org.example.dto.Task;
import org.example.dto.TaskExecutionPlan;
import org.example.dto.TaskOutput;
import org.example.orchestrator.LoopOrchestration;
import org.example.orchestrator.Orchestrator;
import org.example.orchestrator.ParallelOrchestration;
import org.example.orchestrator.SequentialOrchestrator;
import org.example.dto.WorkflowType;
import org.example.parser.SysMLParser;

import java.util.ArrayList;
import java.util.List;

public class CollaborationAgent implements MosaicoAgent{
    private final List<Orchestrator> orchestrators = List.of(new SequentialOrchestrator(this), new ParallelOrchestration(this), new LoopOrchestration(this));
    private CollaborationAgent managerAgent;
    private List<CollaborationAgent> orchestrationAgentPool;
    private List<MosaicoAgent> agentPool;

    public CollaborationAgent() {
        this.managerAgent = null;
    }

    public CollaborationAgent(CollaborationAgent managerAgent) {
        this.managerAgent = managerAgent;
    }
    
    public String getAgentCard() {
        return "";
    }
    
    public String run(String collaborationPattern) {
        TaskExecutionPlan executionPlan = SysMLParser.parse(collaborationPattern);
        var taskOutputs = runOrchestrator(executionPlan.tasks(), executionPlan.taskExecutionPlans(), executionPlan.workflowType(), new ArrayList<>());
        return taskOutputs.toString();
    }

    public List<TaskOutput> runOrchestrator(List<Task> tasks, List<TaskExecutionPlan> taskExecutionPlans, WorkflowType workflowType, List<TaskOutput> taskOutputs) {
        var output = new ArrayList<TaskOutput>();
        for (var orchestrator : orchestrators) {
            if(orchestrator.getWorkflowType() == workflowType) {
                output.addAll(orchestrator.run(tasks, taskExecutionPlans, taskOutputs));
            }
        }
        return output;
    }

    public List<String> generateKeyWordsForTask(String taskDescription) {
        return List.of();
    }

    // REPO
    public List<MosaicoAgent> retrieveKeywordsAgents(List<String> keywords) {
        return List.of();
    }

    // REPO ? ou AgentPool
    public static MosaicoAgent findBestAgentForTask(List<MosaicoAgent> agentPool, List<String> taskKeywords, String taskDescription) {
        return null;
    }

    public TaskExecutionPlan buildTaskExecutionPlan(Task task, WorkflowType workflowType) {
        return null;
    }
    
    public String executeOrchestrator(Orchestrator orchestrator, TaskExecutionPlan workflowTaskExecutionPlan) {
        return "";
    }
}
