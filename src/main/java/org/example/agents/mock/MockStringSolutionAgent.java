package org.example.agents.mock;

import org.example.agents.mosaico.MosaicoAgent;
import org.example.agents.mosaico.SolutionAgent;
import org.example.dto.task.AgentTask;
import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.Channel;
import org.example.dto.task.output.StringValue;
import org.example.dto.task.output.Value;

import java.util.List;

public class MockStringSolutionAgent extends MockSolutionAgent {


    public static final Value OUTPUT = new StringValue("bla bla");

    public MockStringSolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
    }

    @Override
    Value mockOutput(){
        return OUTPUT ;
    }

}
