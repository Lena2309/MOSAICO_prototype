package org.example.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

public class LLMOpenAI implements LLM {

    static final String OPENAI_API_KEY = System.getenv().getOrDefault("OPENAI_API_KEY", "demo");

    final OpenAiChatModel chatModel;

    public LLMOpenAI(){
        this.chatModel = OpenAiChatModel.builder()
                .apiKey(LLMOpenAI.OPENAI_API_KEY)
                .modelName("gpt-4o-mini")
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    public String chat(SystemMessage sm, UserMessage um){
        return this.chatModel.chat(sm, um).aiMessage().text();
    }
}
