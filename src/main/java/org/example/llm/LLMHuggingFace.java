package org.example.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

public class LLMHuggingFace implements LLM {

    final OpenAiChatModel chatModel;

    public LLMHuggingFace(){
        this.chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("HUGGING_FACE_KEY"))
                .baseUrl("https://router.huggingface.co/v1")
                .modelName("zai-org/GLM-5.1")
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    public String chat(SystemMessage sm, UserMessage um){
        //System.out.println("[DEBUG][REQUEST TO LLM]");
        ChatResponse chat = this.chatModel.chat(sm, um);
        System.out.println("[LOG] " + chat.tokenUsage().toString());
        String res =  chat.aiMessage().text();
        System.out.println("[DEBUG][LLM ANSWER] " + res);
        return res ;
    }
}
