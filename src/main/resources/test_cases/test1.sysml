// Test case : conjunction operator (&) in loop condition

private import ScalarValues::*;
private import Collections::*;
private import CollectionFunctions::*;
public import MOSAICO::*;

package RequirementManagementAgentsNoParams {
    part def RequirementManagerAgent specializes SolutionAgent {
        :>> id = "eu.mosaico-project.requirement-manager-agent-type.v1";
        constraint { licence == "MIT" } // licences is not defined
    }

    part def CheckCorrectNessAgent specializes SupervisionAgent {
        :>> id = "eu.mosaico-project.check-correctness-agent-type.v1";
    }

    part def CheckNonRedundancyAgent specializes SupervisionAgent {
        :>> id = "eu.mosaico-project.check-non-redundancy-agent-type.v1";
    }

    part def CheckSatisfiabilityAgent specializes SupervisionAgent {
        :>> id = "eu.mosaico-project.check-satisfiability-agent-type.v1";
    }

    referenceAgent : ReferenceAgent;
    requirementManagerAgent : RequirementManagerAgent;
    checkCorrectnessAgent : CheckCorrectNessAgent;
    checkNonRedundancyAgent : CheckNonRedundancyAgent;
    checkSatisfiabilityAgent : CheckSatisfiabilityAgent;
    consensusAgent: ConsensusAgent;
    evaluatorAgent : MockSequenceEvaluatorAgent ;
    otherEvaluatorAgent : MockTrueEvaluatorAgent ;

    action def GenerateRequirementsProcess {
        
        first start;
        
        then action userInput : Step {
            :>> description = "Get a system description from the user.";
            :>> agent = referenceAgent;
            out biduleOUT : String;
        }

        then loop action constructRequirementList  {

            action decompose : Step {
                :>> description = "A new requirement is generated based on the system description.";
                :>> agent = requirementManagerAgent;
                out req ;
            }

            then fork; then checkCorrectness; then checkNonRedundancy; then checkSatisfiability;
            
            action checkCorrectness : Step {
                :>> description = "Checks whether the new Requirement is correct based on the system description.";
                :>> agent = otherEvaluatorAgent ;
                out correct : Boolean;
            } then joinNode;

            action checkNonRedundancy : Step {
                :>> description = "Checks that the new requirement is not redundant based on the current list of accepted requirements";
                out nonRedundant : Boolean;
            } then joinNode;

            action checkSatisfiability : Step {
                :>> description = "Checks whether the new requirement is satisfiable.";
                out satisfiable : Boolean;
            } then joinNode;

            then join joinNode;

            then action checkConsensus : Step {
                :>> agent = consensusAgent;
                in correct : Boolean;
                in nonRedundant : Boolean;
                in satisfiable : Boolean;
                out consensus : Boolean;
            }

            then action updateRequirements : Step {
                :>> description = "Updates the list of accepted requirements and checks whether the list of accepted requirements is complete.";
                :>> agent = evaluatorAgent;
                in consensus : Boolean;
                out isComplete : Boolean;
            }
        } until (constructRequirementList.updateRequirements.isComplete & constructRequirementList.checkCorrectness.correct) ;

        then action outputToUser : Step {
            :>> description = "Returns the list of completed requirements to the user.";
            :>> agent = referenceAgent;
            in biduleOUT : String;
            out userOutput;
        }

        then done;
    }
}
