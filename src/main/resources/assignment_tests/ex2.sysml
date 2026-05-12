package IntermediateAssignment {
    private import ScalarValues::*;
    private import MOSAICO::*;
    private import SequenceFunctions::*;

    action def TestAssignment {
        attribute requirements : String[0..*] := ();
        first start;

        then action userInput : ReferenceStep {
            :>> description = "Act as a user providing an initial system description. Output a description for a system that verifies whether a given word is a palindrome to the systemDescription channel. For the requirements channel, respond only with a blank string.";
            out systemDescription : String;
        }

        then action decompose : AgentStep {
            :>> description = "A new requirement is generated based on the system description.";
            in systemDescription : String;
            out newRequirement : String;
        }

        then action assignment {
            in newRequirement : String;
            assign requirements := requirements->including(newRequirement);
        }

        then done;
    }
}