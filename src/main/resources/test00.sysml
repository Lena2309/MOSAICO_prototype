private import ScalarValues::*;
private import Collections::*;
private import CollectionFunctions::*;
public import MOSAICO::*;

package RequirementManagementAgentsNoParams {
    part def RequirementManagerAgent specializes SolutionAgent {
        :>> id = "eu.mosaico-project.requirement-manager-agent-type.v1";
        constraint { licence == "MIT" } // licences is not defined
    }

    part def CheckCorrectnessAgent specializes SupervisionAgent {
        :>> id = "eu.mosaico-project.check-correctness-agent-type.v1";
    }

    referenceAgent : ReferenceAgent;
    requirementManagerAgent : RequirementManagerAgent;
    checkCorrectnessAgent : CheckCorrectnessAgent;
    evaluatorManagerAgent : EvaluatorAgent ;

    consensusAgent: ConsensusAgent;


    action def GenerateRequirementsProcess {
        
        first start;
        
        then action userInput : Step {
            :>> description = "Get a system description from the user.";
            :>> agent = referenceAgent;
            :>> specification = "A function to compare two cities given their population.";
            out specification : String;
        }

        then loop action constructRequirementList  {

            action decompose : Step {
                :>> description = "A new requirement is generated based on the system description.";
                :>> agent = requirementManagerAgent;
                out req : String;
            }

            action checkCorrectness : Step {
                :>> description = "Checks whether the new Requirement is correct based on the system description.";
                :>> agent = checkCorrectnessAgent;
                out correct : Boolean;
            }

            then action checkConsensus : Step {
                :>> agent = consensusAgent;
                in correct : Boolean;
                out consensus : Boolean;
            }

            then action updateRequirements : Step {
                :>> description = "Updates the requirements and checks its completeness.";
                :>> agent = evaluatorManagerAgent;
                in consensus : Boolean;
                out isComplete : Boolean;
            }
        } until isComplete;

        then action outputToUser : Step {
            :>> description = "Returns the list of completed requirements to the user.";
            :>> agent = referenceAgent;
            in specification : String;
            out stdout : String
        }

        then done;
    }
}
