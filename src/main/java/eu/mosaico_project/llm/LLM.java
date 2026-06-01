package eu.mosaico_project.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;


public interface LLM {
    String chat(SystemMessage sm, UserMessage um);
}
