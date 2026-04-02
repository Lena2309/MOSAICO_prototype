package RequirementManagementAgents {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    part def RequirementManagerAgent specializes SolutionAgent {
        :>> id = "eu.mosaico-project.requirement-manager-agent-type.v1";
        constraint { licences->includes("MIT") }
    }

    part def CheckCorrectNessAgent specializes SolutionAgent {
        :>> id = "eu.mosaico-project.check-correctness-agent-type.v1";
    }

    part def CheckNonRedundancyAgent specializes SolutionAgent {
        :>> id = "eu.mosaico-project.check-non-redundancy-agent-type.v1";
    }

    part def CheckSatisfiabilityAgent specializes SolutionAgent {
        :>> id = "eu.mosaico-project.check-satisfiability-agent-type.v1";
    }

    referenceAgent : SolutionAgent;
    requirementManagerAgent : RequirementManagerAgent;
    checkCorrectnessAgent : CheckCorrectNessAgent;
    checkNonRedundancyAgent : CheckNonRedundancyAgent;
    checkSatisfiabilityAgent : CheckSatisfiabilityAgent;
    consensusAgent : SolutionAgent;

    action def GenerateRequirementsProcess {
        attribute requirements :String := ();
        first start;

        then action userInput : ReferenceStep {
            :>> description = "Get a system description from the user. For the system description channel, respond with the system description of a Palindrome function (that verifies if a word is a palindrome). For the requirements channel, respond with a blank string.";
            :>> agent = referenceAgent;
            out systemDescription : String;
            out requirements : String;
        }

        then loop action constructRequirementList {
            in systemDescription : String;
            inout requirements : String;

            action decompose : AgentStep {
                :>> description = "A new requirement is generated based on the system description. Make it simple, do not cover everything in one requirement. This will be executed multiple times so do not cover everything.";
                :>> agent = requirementManagerAgent;
                in systemDescription : String;
                out newRequirement : String;
            }

            then fork;
                then checkCorrectness;
                then checkNonRedundancy;
                then checkSatisfiability;

            action checkCorrectness : SupervisionStep {
                :>> description =
                    "Checks whether the new Requirement is correct based on the system description.";
                :>> agent = checkCorrectnessAgent;
                in newRequirement : String;
                in systemDescription : String;
                out isCorrect : Boolean;
            }
            then joinNode;

            action checkNonRedundancy : SupervisionStep {
                :>> description =
                    "Checks that the new requirement is not redundant based on the current list of accepted requirements";
                :>> agent = checkNonRedundancyAgent;
                in newRequirement : String;
                in systemDescription : String;
                in requirements :String;
                out isNonRedundant : Boolean;
            }
            then joinNode;

            action checkSatisfiability : SupervisionStep {
                :>> description = "Checks whether the new requirement is satisfiable.";
                :>> agent = checkSatisfiabilityAgent;
                in newRequirement : String;
                in systemDescription : String;
                in requirements :String;
                out isSatisfiable : Boolean;
            }
            then joinNode;

            join joinNode;

            then action checkConsensus : ConsensusStep {
                :>> description = "Checks if all the checks were true.";
                :>> agent = consensusAgent;
                in isCorrect : Boolean;
                in isNonRedundant : Boolean;
                in isSatisfiable : Boolean;
                out allValid : Boolean;
            }

            then action updateRequirements : AgentStep {
                :>> description =
                    "Append the new requirement channel value to the latest requirements channel ONLY if the latest allValid channel is true. To update, you have to rewrite the entire original string and then add the new requirement at the end. For the isComplete channel, check whether the latest requirements channel is complete based on the system description channel, meaning it covers every aspect of the system description.";
                :>> agent = requirementManagerAgent;
                inout requirements :String;
                in newRequirement : String;
                in systemDescription : String;
                out isComplete : Boolean;
            }
        } until constructRequirementList.updateRequirements.isComplete;

        then action outputToUser : AgentStep {
            :>> description = "Returns the list of completed requirements to the user.";
            :>> agent = referenceAgent;
        }

        then done;
    }
}
