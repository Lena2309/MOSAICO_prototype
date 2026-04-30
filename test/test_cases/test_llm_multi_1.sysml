// Test case : llm agent that generates a list.

package TestConditionTrue {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    splitterAgent : SolutionAgent;

    action def Process {

        first start;

        then action identifyNouns : AgentStep {
            :>> description = "Give me a list of 5 keywords in Java, separated by a comma.";
            :>> agent = splitterAgent;
            out nouns : String [0..*];
        }

        then done;
    }
}
