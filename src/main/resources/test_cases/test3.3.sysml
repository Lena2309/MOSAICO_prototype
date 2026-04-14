// Test case : behavior of literal 'true' in 'while' loop condition
// Expected behavior : the loop does not terminate and is halted by the system.

private import ScalarValues::*;
private import Collections::*;
private import CollectionFunctions::*;
public import MOSAICO::*;

package Test3.2 {

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

        then while true action constructRequirementList  {

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
        }

        then action outputToUser : AgentStep {
            :>> description = "Test KO";
            :>> agent = referenceAgent;
             in req : Boolean ;
        }

        then done;
    }
}
