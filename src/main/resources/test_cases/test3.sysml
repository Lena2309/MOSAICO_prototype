// Test case : behavior of literal 'false' in loop condition

private import ScalarValues::*;
private import Collections::*;
private import CollectionFunctions::*;
public import MOSAICO::*;

package RequirementManagementAgentsNoParams {

    part def MockTrueAgent specializes SolutionAgent {
        :>> id = "mock.true"
    }

    part def MockStringAgent specializes SolutionAgent {
        :>> id = "mock.bla"
    }

    part def RequirementManagerAgent specializes MockStringAgent {
        :>> id = "eu.mosaico-project.requirement-manager-agent-type.v1";
        constraint { licence == "MIT" } // licences is not defined
    }

    part def CheckCorrectNessAgent specializes MockTrueAgent {
        :>> id = "eu.mosaico-project.check-correctness-agent-type.v1";
    }

    part def CheckNonRedundancyAgent specializes MockTrueAgent {
        :>> id = "eu.mosaico-project.check-non-redundancy-agent-type.v1";
    }

    part def CheckSatisfiabilityAgent specializes MockTrueAgent {
        :>> id = "eu.mosaico-project.check-satisfiability-agent-type.v1";
    }

    part def CompletionEvaluatorAgent specializes MockTrueAgent {
         :>> id = "eu.mosaico-project.check-cp-eval-agent-type.v1";
    }
    part def OtherCompletionEvaluatorAgent specializes MockTrueAgent {
             :>> id = "eu.mosaico-project.check-alt-cp-eval-agent-type.v1";
        }



    referenceAgent : ReferenceAgent;
    requirementManagerAgent : RequirementManagerAgent;
    checkCorrectnessAgent : CheckCorrectNessAgent;
    checkNonRedundancyAgent : CheckNonRedundancyAgent;
    checkSatisfiabilityAgent : CheckSatisfiabilityAgent;
    consensusAgent: ConsensusAgent;
    evaluatorAgent :      CompletionEvaluatorAgent;
    otherEvaluatorAgent : OtherCompletionEvaluatorAgent;

    action def GenerateRequirementsProcess {
        
        first start;
        
        then action userInput : AgentStep {
            :>> description = "Get a system description from the user.";
            :>> agent = referenceAgent;
            out biduleOUT : String;
        }

        then loop action constructRequirementList  {

            action decompose : AgentStep {
                :>> description = "A new requirement is generated based on the system description.";
                :>> agent = requirementManagerAgent;
                out req ;
            }

            then fork; then checkCorrectness; then checkNonRedundancy; then checkSatisfiability;
            
            action checkCorrectness : AgentStep {
                :>> description = "Checks whether the new Requirement is correct based on the system description.";
                :>> agent = otherEvaluatorAgent ;
                out correct : Boolean;
            } then joinNode;

            action checkNonRedundancy : AgentStep {
                :>> description = "Checks that the new requirement is not redundant based on the current list of accepted requirements";
                :>> agent = checkNonRedundancyAgent ;
                out nonRedundant : Boolean;
            } then joinNode;

            action checkSatisfiability : AgentStep {
                :>> description = "Checks whether the new requirement is satisfiable.";
                :>> agent = checkSatisfiabilityAgent ;
                out satisfiable : Boolean;
            } then joinNode;

            then join joinNode;

            then action checkConsensus : AgentStep {
                :>> agent = consensusAgent;
                in correct : Boolean;
                in nonRedundant : Boolean;
                in satisfiable : Boolean;
                out consensus : Boolean;
            }

            then action updateRequirements : AgentStep {
                :>> description = "Updates the list of accepted requirements and checks whether the list of accepted requirements is complete.";
                :>> agent = evaluatorAgent;
                in consensus : Boolean;
                out isComplete : Boolean;
            }
        } until (constructRequirementList.updateRequirements.isComplete & false) ;

        then action outputToUser : AgentStep {
            :>> description = "Returns the list of completed requirements to the user.";
            :>> agent = referenceAgent;
            in biduleOUT : String;
            out userOutput;
        }

        then done;
    }
}
