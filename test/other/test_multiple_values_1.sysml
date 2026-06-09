// Test case : behavior of value 'true' in 'if' condition

package TestConditionTrue {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    part def EvaluatorAgent specializes MockTrueAgent {
            :>> id = "eval1" ;
       }

    part def GeneratorAgent specializes MockMultipleValueAgent {
         :>> id = "gen1" ;
    }

    referenceAgent : ReferenceAgent;
    evaluatorAgent : EvaluatorAgent;
    generatorAgent : GeneratorAgent;

    action def Process {

        first start;

        then action generation : AgentStep {
            :>> description = "Generate Something.";
            :>> agent = generatorAgent ;
            out generated : String [0..*];
        }

        then action evaluation : AgentStep {
            :>> description = "Check something." ;
            :>> agent = evaluatorAgent ;
            in generated : String [0..*] ;
            out result : Boolean;
        }

        then action showResults : AgentStep {
            :>> description = "Final results.";
            :>> agent = referenceAgent;
            in result : Boolean ;
            in generated : String[0..n];
        }

        then done;
    }
}
