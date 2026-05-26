package org.example.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

public class MockLLM implements LLM {

    final String reply ;

    public MockLLM(String reply) {
        this.reply = reply ;
    }

    @Override
    public String chat(SystemMessage sm, UserMessage um) {
        return this.reply;
    }
}
