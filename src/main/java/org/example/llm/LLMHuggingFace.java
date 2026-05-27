package org.example.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

public class LLMHuggingFace implements LLM {

    final OpenAiChatModel chatModel;

    public LLMHuggingFace(){
        //this.chatModel = buildMistral() ;
        this.chatModel = buildZAI() ;
    }

    /** Get a model based on Mistral from a HuggingFace provider. */
    static OpenAiChatModel buildMistral(){
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("HUGGING_FACE_KEY"))
                .baseUrl("https://router.huggingface.co/featherless-ai/v1")
                .modelName("mistralai/Mistral-7B-Instruct-v0.1")
                .maxCompletionTokens(200)
                .maxTokens(200)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    /** Get a model based on ZAI from a HuggingFace provider. */
    static OpenAiChatModel buildZAI(){
        return OpenAiChatModel.builder()
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
