// Test case : behavior of literal 'false' in 'while' loop condition
// Expected behavior : the loop terminates without executing its body.

private import ScalarValues::*;
private import Collections::*;
private import CollectionFunctions::*;
public import MOSAICO::*;

package Test3_2 {

    part def GeneratorAgent specializes MockStringAgent {
        :>> id = "gen1" ;
        constraint { licence == "MIT" }
    }

    part def EvaluatorAgent specializes MockTrueAgent {
         :>> id = "eval1";
    }

    referenceAgent : ReferenceAgent;
    generatorAgent : GeneratorAgent;
    evaluatorAgent : EvaluatorAgent;

    action def GenerateRequirementsProcess {
        
        first start;

        then while false action constructRequirementList  {

            action decompose : AgentStep {
                :>> description = "Generate a new requirement." ;
                :>> agent = generatorAgent;
                out req ;
            }

            then action updateRequirements : AgentStep {
                :>> description = "Evaluate the list of requirements." ;
                :>> agent = evaluatorAgent;
                out isComplete : Boolean;
            }

            then action outputToUser : AgentStep {
                :>> description = "Test KO";
                :>> agent = referenceAgent;
                 in req : Boolean ;
            }
        }

        then action outputToUser : AgentStep {
            :>> description = "Test OK (if not entered into the loop body).";
            :>> agent = referenceAgent;
             in req : Boolean ;
        }

        then done;
    }
}
