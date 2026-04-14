// Test case : behavior of FeatureChain (dot notation) in loop condition

private import ScalarValues::*;
private import Collections::*;
private import CollectionFunctions::*;
public import MOSAICO::*;

package Test0 {

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
        } until constructRequirementList.updateRequirements.isComplete; // FIXME : prefix not used

        then action outputToUser : AgentStep {
            :>> description = "Test OK";
            :>> agent = referenceAgent;
            in req : String ;
        }

        then done;
    }
}
