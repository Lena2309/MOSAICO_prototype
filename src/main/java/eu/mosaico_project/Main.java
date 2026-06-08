package eu.mosaico_project;

import eu.mosaico_project.agents.mosaico.CollaborationAgent;
import eu.mosaico_project.dto.step.Step;
import eu.mosaico_project.transformer.SysMLDecoder;

public class Main {
    static void main(String[] args) {

        IO.println(String.format("Hello and welcome!"));

        final String inputFile ;
        if (args.length == 0) {
            System.out.println("No parameter found, using the default input file.");
            inputFile = "src/main/resources/req1.sysml";
        }
        else
            inputFile = args[0];

        Step parsed = SysMLDecoder.decode(inputFile);
        System.out.println("\n" + "Plan:");
        System.out.println(parsed.toString());
        var collabAgent = new CollaborationAgent();
        var outputsString = collabAgent.run(parsed);
        System.out.println("\nFinal Stack Trace:\n" + outputsString);
    }
}
