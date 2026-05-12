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
            attribute requirements : String[0..*] := ();
            first start;

            then action userInput : ReferenceStep {
                :>> description = "Act as a user providing an initial system description. Output a description for a system that verifies whether a given word is a palindrome to the systemDescription channel. For the requirements channel, respond only with a blank string.";
                out systemDescription : String;
            }

            then action decompose : AgentStep {
                :>> description = "A new requirement is generated based on the system description.";
                :>> agent = requirementManagerAgent;
                in systemDescription : String;
                out newRequirement : String;
            }

            then action assignment {
                in newRequirement : String;
                assign requirements := requirements->including(newRequirement);
            }

            then done;
        }
}
