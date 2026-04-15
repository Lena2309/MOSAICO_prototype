// Test case : behavior of FeatureChain (dot notation) in loop condition with bad reference
// Expected behavior : Either syntactically rejected because of undefined reference, or infinite loop.

private import ScalarValues::*;
private import Collections::*;
private import CollectionFunctions::*;
public import MOSAICO::*;

package Test0_2 {

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

    action def GenerateRequirementsProcess {
        
        first start;

        then loop action constructRequirementList  {

            action generate : AgentStep {
                :>> description = "Generate a new requirement." ;
                :>> agent = generatorAgent;
                out req ;
            }

            then action evaluate : AgentStep {
                :>> description = "Evaluate the list of requirements." ;
                :>> agent = evaluatorAgent;
                in req ;
                out isComplete : Boolean;
            }
        } until constructRequirementList.updateRequirements.isComplete; // bad prefix

        then action outputToUser : AgentStep {
            :>> description = "Test K0";
            :>> agent = referenceAgent;
            in req : String ;
        }

        then done;
    }
}
