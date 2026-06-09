package eu.mosaico_project.agents.mosaico;

import eu.mosaico_project.miol.ChannelState;
import eu.mosaico_project.miol.task.AgentTask;
import eu.mosaico_project.miol.task.output.Channel;
import eu.mosaico_project.miol.task.output.TaskOutput;
import eu.mosaico_project.miol.task.output.value.MultipleValue;
import eu.mosaico_project.miol.task.output.value.StringValue;
import eu.mosaico_project.miol.task.output.value.Value;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ReferenceAgent extends MosaicoAgent {
    public ReferenceAgent() {
        super(String.valueOf(UUID.randomUUID()), "Reference Agent", null, null);
    }

    public ReferenceAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, null, constraints);
    }

    @Override
    public TaskOutput performTask(AgentTask task, ChannelState dependencies, Channel channel) {
        throw new InvalidParameterException("Reference Agents do not perform tasks.");
    }

    public void showToUser(String s) {
        System.out.println("[OUTPUT] " + s);
    }

    public Value askToUser(String comment, Channel c) {
        System.out.println("[INPUT REQUIRED] " + comment);
        System.out.println("[INPUT REQUIRED] " + c);

        Scanner scanner = new Scanner(System.in);
        Value v;
        if (c.multiple()) {
            System.out.println("Your input: (multiple input, end with EOF)");
            v = new MultipleValue();
            while (scanner.hasNext())
                ((MultipleValue) v).addValue(new StringValue(scanner.nextLine()));
        } else {
            System.out.println("Your inputs: (single input, end with NEWLINE)");
            v = new StringValue(scanner.nextLine());
        }
        return v;
    }
}
