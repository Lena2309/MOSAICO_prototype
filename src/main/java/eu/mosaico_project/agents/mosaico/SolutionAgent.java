package eu.mosaico_project.agents.mosaico;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import eu.mosaico_project.dto.ChannelState;
import eu.mosaico_project.dto.task.AgentTask;
import eu.mosaico_project.dto.task.output.Channel;
import eu.mosaico_project.dto.task.output.TaskOutput;
import eu.mosaico_project.dto.task.output.value.BooleanValue;
import eu.mosaico_project.dto.task.output.value.MultipleValue;
import eu.mosaico_project.dto.task.output.value.StringValue;
import eu.mosaico_project.dto.task.output.value.Value;
import eu.mosaico_project.llm.LLM;
import eu.mosaico_project.llm.LLMProvider;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class SolutionAgent extends MosaicoAgent {
    final LLM llm;

    public SolutionAgent(String id, String name, String description, List<String> constraints) {
        super(id, name, null, constraints);
        this.llm = LLMProvider.get(LLMProvider.DEFAULT);
    }

    /**
     * Decode the answer of an LLM into a single value.
     */
    static Value decodeSingle(String generatedText, String t) {
        return switch (t) {
            case "String" -> new StringValue(generatedText);
            case "Boolean" -> new BooleanValue(generatedText);
            default -> throw new InvalidParameterException("No Channel to output or not supported type:" + t);
        };
    }

    /**
     * Decode the answer of an LLM into a multiple value.
     */
    static MultipleValue decodeMultiple(String generatedText, String t, String separator) {
        var res = new MultipleValue();
        if (generatedText == null) return res;
        var tab = generatedText.split(separator);
        switch (t) {
            case "String" -> {
                for (String s : tab) res.addValue(new StringValue(s));
            }
            case "Boolean" -> {
                for (String s : tab) res.addValue(new BooleanValue(s));
            }
            default -> throw new InvalidParameterException("No Channel to output or not supported type:" + t);
        }
        return res;
    }

    @Override
    public TaskOutput performTask(AgentTask task, ChannelState dependencies, Channel channel) {
        // 1. Build the context and prompt
        String promptContext = buildContext(dependencies);
        String finalPrompt = "Task Description:\n" + task.getTaskDescription() + "\n\n" + promptContext
                + "You are working on the channel " + channel.name() + ", of type " + channel.type() + ".";

        // 2. Create the messages
        var systemMessage = SystemMessage.from("You are a helpful AI agent executing a workflow task. Reply only with your answer, with no explanation and in no particular format.");
        var userMessage = UserMessage.from(finalPrompt);

        // 3. Execute the LLM call using LangChain4j

        System.out.println("[SOLUTION AGENT][PROMPT] (" + this.getName() + ")" + finalPrompt);
        System.out.println("[SOLUTION AGENT] (" + this.getName() + ") Waiting for LLM response.");
        String generatedText = llm.chat(systemMessage, userMessage);

        // 4. Wrap and return the output
        Value resultValue;
        String t = channel.type().orElse("String"); // If the type is unspecified, keep the String.
        if (!channel.multiple()) {
            resultValue = decodeSingle(generatedText, t);
        } else {
            Optional<String> separator = task.getOtherProperty("separator");
            resultValue = decodeMultiple(generatedText, t, separator.orElse(", *"));
        }

        return new TaskOutput(task, channel, resultValue);
    }

    /**
     * Stringifies the outputs of dependent tasks to inject into the LLM's context.
     */
    static String buildContext(List<TaskOutput> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return "";
        }

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Context from previous dependencies:\n");

        for (TaskOutput dep : dependencies) {
            contextBuilder.append("--- Output from Channel: ")
                    .append(dep.channel().name())
                    .append(" ---\n")
                    .append(dep.value().print())
                    .append("\n\n");
        }

        return contextBuilder.toString();
    }
}