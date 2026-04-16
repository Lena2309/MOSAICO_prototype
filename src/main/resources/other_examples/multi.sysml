package RequirementManagementAgents {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    part def RequirementGenerator specializes MockStringAgent {
        :>> id = "eu.mosaico-project.requirement-manager-agent-type.v1";
        constraint { licences->includes("MIT") }
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

    referenceAgent : ReferenceAgent;
    requirementGenerator : RequirementGenerator;
    checkCorrectnessAgent : CheckCorrectNessAgent;
    checkNonRedundancyAgent : CheckNonRedundancyAgent;
    checkSatisfiabilityAgent : CheckSatisfiabilityAgent;
    consensusAgent : MockMultipleValueAgent;
    checkCompletenessAgent : MockTrueAgent ;

    action def GenerateRequirementsProcess {
        first start;

        then action userInput : AgentStep {
            :>> description = "Input a system description. For the system description channel, respond with the specification of the system. For the requirements channel, respond with the list of initial requirements.";
            :>> agent = referenceAgent;
            out systemDescription : String;
            out requirements : String [0..*];
        }

        then loop action constructRequirementList {
            in systemDescription : String;
            inout requirements : String [0..*];

            action decompose : AgentStep {
                :>> description = "A new requirement is generated based on the system description. Make it simple, do not cover everything in one requirement. This will be executed multiple times so do not cover everything.";
                :>> agent = requirementGenerator;
                in systemDescription : String;
                out newRequirement : String [0..*];
            }

            then fork;
                then checkCorrectness;
                then checkNonRedundancy;
                then checkSatisfiability;

            action checkCorrectness : AgentStep {
                :>> description =
                    "Checks whether the new Requirement is correct based on the system description.";
                :>> agent = checkCorrectnessAgent;
                in newRequirement : String;
                in systemDescription : String;
                out isCorrect : Boolean;
            }
            then joinNode;

            action checkNonRedundancy : AgentStep {
                :>> description =
                    "Checks that the new requirement is not redundant based on the current list of accepted requirements";
                :>> agent = checkNonRedundancyAgent;
                in newRequirement : String;
                in systemDescription : String;
                in requirements : String [0..*];
                out isNonRedundant : Boolean;
            }
            then joinNode;

            action checkSatisfiability : AgentStep {
                :>> description = "Checks whether the new requirement is satisfiable.";
                :>> agent = checkSatisfiabilityAgent;
                in newRequirement : String;
                in systemDescription : String;
                in requirements : String [0..*];
                out isSatisfiable : Boolean;
            }
            then joinNode;

            join joinNode;

            then action updateAcceptedResuirements : AgentStep {
                :>> description = "Update the list of accepted requirements if all the checks were true.";
                :>> agent = consensusAgent;
                in isCorrect : Boolean;
                in isNonRedundant : Boolean;
                in isSatisfiable : Boolean;
                in newRequirement : String ;
                inout requirements : String [0..*] ;
            }

            then action checkCompleteness : AgentStep {
                :>> description =
                    "Check whether the latest requirements channel is complete based on the system description channel, meaning it covers every aspect of the system description.";
                :>> agent = checkCompletenessAgent;
                in requirements :String [0..*];
                in systemDescription : String;
                out isComplete : Boolean;
            }
        } until constructRequirementList.checkCompleteness.isComplete;

        then action outputToUser : AgentStep {
            :>> description = "Returns the list of completed requirements to the user.";
            :>> agent = referenceAgent;
            in requirements : String [0..*];
        }

        then done;
    }
}

