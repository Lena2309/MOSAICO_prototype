package org.example.agents.mosaico;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import org.example.dto.task.AgentTask;
import org.example.dto.task.AgentTaskOutput;
import org.example.dto.task.output.*;
import org.example.llm.LLM;
import org.example.llm.LLMHuggingFace;
import org.example.llm.LLMOpenAI;

import java.security.InvalidParameterException;
import java.util.List;

public class SolutionAgent extends MosaicoAgent {

    final LLM llm ;

    public SolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, description, constraints);
        this.llm = new LLMOpenAI(); // CHOOSE YOUR LLM HERE
        //this.llm = new LLMHuggingFace(); // CHOOSE YOUR LLM HERE
    }

    @Override
    public AgentTaskOutput performTask(AgentTask task, List<AgentTaskOutput> dependencies, Channel channel) {
        // 1. Build the context and prompt
        String promptContext = buildContext(dependencies);
        String finalPrompt = "Task Description:\n" + task.getTaskDescription() + "\n\n" + promptContext
                + "You are working on the channel " + channel.getName() + ", of type " + channel.getType() + ".";

        // 2. Create the messages
        var systemMessage = SystemMessage.from("You are a helpful AI agent executing a workflow task. Reply only with your answer, with no explanation and in no particular format.");
        var userMessage = UserMessage.from(finalPrompt);

        // 3. Execute the LLM call using LangChain4j
        System.out.println("[LOG] " + this.getName() + ": LLM call now.");
        String generatedText = llm.chat(systemMessage, userMessage);

        // 4. Wrap and return the output
        Value resultValue;
        try {
            String t = channel.getType().toLowerCase();
            if (!channel.isMultiple()) {
                resultValue = switch (t) {
                    case "string" -> new StringValue(generatedText);
                    case "boolean" -> new BooleanValue(generatedText.toLowerCase().contains("true"));
                    default -> throw new InvalidParameterException("No Channel to output or not supported type:" + t);
                };
            }
            else{
                var tab = generatedText.split(",");
                var res = new MultipleValue();
                switch (t) {
                    case "string" -> {
                        for (String s : tab)
                            res.addValue(new StringValue(s));
                    }
                    case "boolean" -> {
                        for (String s :tab){
                            var b = new BooleanValue(s.toLowerCase().contains("true"));
                            res.addValue(b);
                        }
                    }
                    default -> throw new InvalidParameterException("No Channel to output or not supported type:" + t);
                }
                resultValue = res ;
            }
        } catch (Exception e) {
            resultValue = new StringValue("Exception while execution: " + e.getMessage());
        }

        return new AgentTaskOutput(task, channel, resultValue);
    }

    /**
     * Stringifies the outputs of dependent tasks to inject into the LLM's context.
     */
    private String buildContext(List<AgentTaskOutput> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return "";
        }

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Context from previous dependencies:\n");

        for (AgentTaskOutput dep : dependencies) {
            contextBuilder.append("--- Output from Channel: ")
                    .append(dep.channel().getName())
                    .append(" ---\n")
                    .append(dep.value().print())
                    .append("\n\n");
        }

        return contextBuilder.toString();
    }
}