// Requirement Manager with reference agent (with mock solution agents and capped number of iterations).

package Example {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;



    referenceAgent : ReferenceAgent;
    generatorAgent : SolutionAgent;
    evaluatorAgent : SolutionAgent;

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
                :>> description = "Given the specification in the channel userSpec, generate an atomic requirement for a software  validating that specification." ;
                :>> agent = generatorAgent;
                in userSpec : String ;
                out req :String ;
            }

            then action evaluation : AgentStep {
                :>> description = "Given the specification in the channel userSpec, and some requirements in the channel req, tell if the requirement covers completely the specification. Answer with TRUE or FALSE only." ;
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
