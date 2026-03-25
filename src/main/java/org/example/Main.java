package org.example;

import org.example.agents.CollaborationAgent;
import org.example.parser.SysMLDecoder;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        IO.println(String.format("Hello and welcome!"));

        var parsed = SysMLDecoder.decode("src/main/resources/test_cases/test0.sysml");
        var collabAgent = new CollaborationAgent();
        collabAgent.run(parsed);
    }
}
