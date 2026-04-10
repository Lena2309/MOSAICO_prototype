// Test case : behavior of conjunction operator (&) in loop condition

private import ScalarValues::*;
private import Collections::*;
private import CollectionFunctions::*;
public import MOSAICO::*;

package Test1 {

    part def GeneratorAgent specializes MockStringAgent {
        :>> id = "gen1" ;
        constraint { licence == "MIT" } // licences is not defined
    }

    part def CorrectnessEvaluatorAgent specializes MockTrueAgent {
        :>> id = "eval1" ;
    }

    part def CompletionEvaluatorAgent specializes MockTrueAgent {
        :>> id = "eval2" ;
    }

    referenceAgent : ReferenceAgent;
    generatorAgent : GeneratorAgent;
    correctnessAgent : CorrectnessEvaluatorAgent;
    completenessAgent : CompletionEvaluatorAgent;


    action def GenerateRequirementsProcess {

        first start;

        then loop action constructRequirementList  {

            action decompose : AgentStep {
                :>> description = "Generate a new requirement." ;
                :>> agent = generatorAgent;
                out req ;
            }

            then action evalCorrect : AgentStep {
                :>> description = "Checks whether the requirements are correct.";
                :>> agent = correctnessAgent ;
                out isCorrect : Boolean;
            }

            then action evalComplete : AgentStep {
                :>> description = "Check that the requirements are complete.";
                :>> agent = completenessAgent;
                out isComplete : Boolean;
            }
        } until (constructRequirementList.evalComplete.isComplete & constructRequirementList.evalCorrect.isCorrect) ;

        then action outputToUser : AgentStep {
            :>> description = "Test OK";
            :>> agent = referenceAgent;
            in biduleOUT : String;
            out userOutput;
        }

        then done;
    }
}
