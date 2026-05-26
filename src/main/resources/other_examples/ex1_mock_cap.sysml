// Requirement Manager with reference agent (with mock solution agents and capped number of iterations).

package Example {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    part def GeneratorAgent specializes MockStringAgent {
        :>> id = "gen1" ;
        constraint { licence == "MIT" }
    }

    part def EvaluatorAgent specializes MockFalseAgent {
         :>> id = "eval1" ;
    }

    referenceAgent : ReferenceAgent;
    generatorAgent : GeneratorAgent;
    evaluatorAgent : EvaluatorAgent;

    action def Process {

        attribute loopIteration : Integer ;

        first start;

        assign loopIteration := 0;

        then action userInput : AgentStep {
            :>> description = "Please give a specification.";
            :>> agent = referenceAgent;
            out userSpec : String ;
        }
        then loop action repeatGeneration {

            action generate : AgentStep {
                :>> description = "Generate a new requirement." ;
                :>> agent = generatorAgent;
                in userSpec : String ;
                out req :String ;
            }

            then action evaluation : AgentStep {
                :>> description = "Check that the generated requirement is correct." ;
                :>> agent = evaluatorAgent ;
                in req ;
                in userSpec : String ;
                out result : Boolean;
            }

             then action incr {
                        assign loopIteration := loopIteration + 1;
                        }

        } until repeatGeneration.evaluation.result | loopIteration > 3 ;

        then action report : AgentStep {
            :>> description = "Requirement successfully generated.";
            :>> agent = referenceAgent;
            in req : String ;
        }

        then done;
    }
}
