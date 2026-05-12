package org.example.llm;

public class LLMProvider {

    /** Get a fresh LLM object based on a description. */
    public static LLM get(String description){
        if (true)
            return new LLMOpenAI();
        else
            return new LLMHuggingFace();

    }
}
