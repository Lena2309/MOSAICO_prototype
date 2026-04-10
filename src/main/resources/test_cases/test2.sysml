// Test case : behavior of literal 'true' in loop condition

private import ScalarValues::*;
private import Collections::*;
private import CollectionFunctions::*;
public import MOSAICO::*;

package Test2 {

    part def GeneratorAgent specializes MockStringAgent {
        :>> id = "gen1" ;
        constraint { licence == "MIT" }
    }

    part def CompletionEvaluatorAgent specializes MockTrueAgent {
         :>> id = "eval1" ;
    }

    referenceAgent : ReferenceAgent;
    generatorAgent : GeneratorAgent;
    evaluatorAgent : CompletionEvaluatorAgent ;

    action def GenerateRequirementsProcess {
        
        first start;

        then loop action constructRequirementList  {

            action decompose : AgentStep {
                :>> description = "Generate a new requirement.";
                :>> agent = generatorAgent;
                out req ;
            }

            then action updateRequirements : AgentStep {
                :>> description = "Evaluate the list of requirements.";
                :>> agent = evaluatorAgent;
                out isComplete : Boolean;
            }
        } until (constructRequirementList.updateRequirements.isComplete & true) ;

        then action outputToUser : AgentStep {
            :>> description = "Test OK";
            :>> agent = referenceAgent;
            in biduleOUT : String;
            out userOutput;
        }

        then done;
    }
}
