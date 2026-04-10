package org.example.agents.mosaico;

import org.example.dto.task.AgentTask;
import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.Channel;

import java.util.Scanner ;
import java.util.List;
import java.util.UUID;

public class ReferenceAgent extends MosaicoAgent {
    public ReferenceAgent() {
        super(String.valueOf(UUID.randomUUID()), "Reference Agent", "An agent that directly interacts with the end-user.", null);
    }

    public ReferenceAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    public AgentTaskOutput callLLM(AgentTask task, List<AgentTaskOutput> dependencies, Channel channel) {
        return null;
    }

    public void showToUser(String s){
        System.out.println("[OUTPUT] " + s);
    }

    public String askToUser(){
        System.out.println("Your input is expected:\n");
        return new Scanner(System.in).nextLine();
    }
}
