package org.example;

import org.example.agents.mosaico.CollaborationAgent;
import org.example.transformer.SysMLDecoder;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        IO.println(String.format("Hello and welcome!"));

        final String inputFile ;
        if (args.length == 0)
            inputFile = "src/main/resources/req1.sysml";
        else
            inputFile = args[0];

        var parsed = SysMLDecoder.decode(inputFile);
        System.out.println(parsed.toString());
        var collabAgent = new CollaborationAgent();
        var outputsString = collabAgent.run(parsed);
        System.out.println("\nFinal Stack Trace:\n" + outputsString);
    }
}
