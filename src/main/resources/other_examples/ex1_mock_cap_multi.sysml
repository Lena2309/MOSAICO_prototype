// Requirement Manager with reference agent (with mock agents and capped number of iterations).

package Example {
    private import ScalarValues::*;
    private import MOSAICO::*;
    private import SequenceFunctions::including;

    // remove-me
    part def GeneratorAgent specializes MockStringAgent {
        :>> id = "gen1" ;
        constraint { licence == "MIT" }
    }

    // remove-me
    part def EvaluatorAgent specializes MockTrueAgent {
         :>> id = "eval1" ;
    }

    // remove-me
    part def NegEvaluatorAgent specializes MockFalseAgent {
         :>> id = "eval2" ;
    }


    referenceAgent : ReferenceAgent;
    generatorAgent : GeneratorAgent;
    evaluatorAgent : EvaluatorAgent;
    otherEvaluatorAgent : NegEvaluatorAgent;

    action def Process {

        attribute loopIteration : Integer ;
        attribute currentRequirements : String[0..*] ;


        action record {
            assign currentRequirements := currentRequirements->including(generate.generatedReq) ;
        }

        action nop {
            assign currentRequirements := currentRequirements ;
        }

        first start;

        assign loopIteration := 0;
        then assign currentRequirements := () ;

        then action userInput : AgentStep {
            :>> description = "Please give a specification.";
            :>> agent = referenceAgent;
            out userSpec : String ;
        }
        then loop action repeatGeneration {

            // fixme: don't move-me at the end of the loop body !
            action incr {
                        assign loopIteration := loopIteration + 1;
                        }

            then action generate : AgentStep {
                :>> description = "Given the specification in the channel userSpec, generate an atomic requirement for a software validating that specification." ;
                :>> agent = generatorAgent;
                in userSpec : String ;
                out generatedReq :String ;
            }

            then action correctnessEvaluation : AgentStep {
                :>> description = "Given the specification in the channel userSpec, and a requirement in the channel generatedReq, tell if the requirement is correct with respect to the specification. Answer with TRUE or FALSE only." ;
                :>> agent = evaluatorAgent ;
                in generatedReq ;
                in userSpec : String ;
                out correctness : Boolean;
                }



            // Everything after the if/then/else is discarded.
            then action evaluation : AgentStep {
                :>> description = "Given the specification in the channel userSpec, and some requirements in the channel currentRequirements, tell if the requirements cover completely the specification. Answer with TRUE or FALSE only." ;
                :>> agent = otherEvaluatorAgent ;
                in currentRequirements ;
                in userSpec : String ;
                out completeness : Boolean;
            }

            // fixme: Everything after that will be ignored
            if correctnessEvaluation.correctness then record ;
                        //else nop;


        } until repeatGeneration.evaluation.completeness | loopIteration > 3 ;

        then action report : AgentStep {
            :>> description = "Requirement successfully generated.";
            :>> agent = referenceAgent;
            in req : String ;
        }

        then done;
    }
}
