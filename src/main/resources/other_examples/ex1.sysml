// Requirement Manager with reference agent

package Example {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    part def GeneratorAgent specializes MockStringAgent {
        :>> id = "gen1" ;
        constraint { licence == "MIT" }
    }

    part def EvaluatorAgent specializes MockTrueAgent {
         :>> id = "eval1" ;
    }

    referenceAgent : ReferenceAgent;
    generatorAgent : GeneratorAgent;
    evaluatorAgent : EvaluatorAgent;

    action def Process {

        first start;

        then action userInput : AgentStep {
            :>> description = "The user inputs its specification.";
            :>> agent = referenceAgent;
            out userSpec : String ;
        }
        then loop action repeatGeneration {

            action generate : AgentStep {
                :>> description = "Generate a new requirement." ;
                :>> agent = generatorAgent;
                in userSpec : String ;
                out req ;
            }

            then action evaluation : AgentStep {
                :>> description = "Check that the generated requirement is correct." ;
                :>> agent = evaluatorAgent ;
                in req ;
                out result : Boolean;
            }
        } until repeatGeneration.evaluation.result ;

        then action report : AgentStep {
            :>> description = "Requirement successfully generated.";
            :>> agent = referenceAgent;
            in req : String ;
        }

        then done;
    }
}
