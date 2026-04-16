package org.example.agents.mosaico;

import org.example.dto.task.AgentTask;
import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.Channel;
import org.example.dto.task.output.MultipleValue;
import org.example.dto.task.output.StringValue;
import org.example.dto.task.output.Value;

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
    public AgentTaskOutput performTask(AgentTask task, List<AgentTaskOutput> dependencies, Channel channel) {
        return null;
    }

    public void showToUser(String s){
        System.out.println("[OUTPUT] " + s);
    }

    public Value askToUser(String comment, Channel c){
        System.out.println("[INPUT REQUIRED] " + comment);
        System.out.println("[INPUT REQUIRED] " + c);

        Scanner scanner = new Scanner(System.in);
        Value v ;
        if (c.isMultiple()) {
            System.out.println("Your input: (multiple input, end with EOF)");
            v = new MultipleValue();
            while (scanner.hasNext())
                ( (MultipleValue) v).addValue(new StringValue(scanner.nextLine()));
        }
        else {
            System.out.println("Your inputs: (single input, end with NEWLINE)");
            v = new StringValue(scanner.nextLine());
        }
        return v;
    }
}
