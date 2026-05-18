package org.example.llm;

public class LLMProvider {

    public enum Provider { OpenAI, HuggingFace } ;

    /** Agents that don't need a specific LLM model can use DEFAULT. */
    public static final Provider DEFAULT = Provider.OpenAI ;

    /** Get a fresh LLM object. */
    public static LLM get(Provider p){
        switch (p) {
            case OpenAI:
                return new LLMOpenAI();
            case HuggingFace: default:
                return new LLMHuggingFace();
        }
    }
}
