package org.example;

import org.example.agents.CollaborationAgent;
import org.example.parser.SysMLParser;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        IO.println(String.format("Hello and welcome!"));

        var collaborationPattern = """
                private import ScalarValues::*;
                private import CollectionFunctions::*;
                private import MOSAICO::*;
                
                package RequirementManagementAgents {
                    part def RequirementManagerAgent specializes SolutionAgent {
                        ::> id = "eu.mosaico-project.requirement-manager-agent-type.v1";
                        constraint { licence == "MIT" }
                    }
                
                    part def CheckCorrectNessAgent specializes SupervisionAgent {
                        ::> id = "eu.mosaico-project.check-correctness-agent-type.v1";
                    }
                
                    part def CheckNonRedundancyAgent specializes SupervisionAgent {
                        ::> id = "eu.mosaico-project.check-non-redundancy-agent-type.v1";
                    }
                
                    part def CheckSatisfiabilityAgent specializes SupervisionAgent {
                        ::> id = "eu.mosaico-project.check-satisfiability-agent-type.v1";
                    }
                
                    referenceAgent : ReferenceAgent;
                    requirementManagerAgent : RequirementManagerAgent;
                    checkCorrectnessAgent : CheckCorrectNessAgent;
                    checkNonRedundancyAgent : CheckNonRedundancyAgent;
                    checkSatisfiabilityAgent : CheckSatisfiabilityAgent;
                    consensusAgent : ConsensusAgent;
                
                    action def GenerateRequirementsProcess {
                        first start;
                
                        then action userInput : ReferenceStep {
                            ::> description = "Get a system description from the user.";
                            out systemDescription : String;
                        }
                
                        then loop action constructRequirementList {
                            in systemDescription : String;
                            inout requirements : String [0..*];
                
                            action decompose : Step {
                                redefines description = "A new requirement is generated based on the system description.";
                                ::> agent = requirementManagerAgent;
                                in systemDescription : String;
                                out newRequirement : String;
                            }
                
                            then fork;
                                then checkCorrectness;
                                then checkNonRedundancy;
                                then checkSatisfiability;
                
                            action checkCorrectness : SupervisionStep {
                                ::> description =
                                    "Checks whether the new Requirement is correct based on the system description.";
                                ::> agent = checkCorrectnessAgent;
                                in newRequirement : String;
                                in systemDescription : String;
                                out isCorrect : Boolean;
                            }
                            then joinNode;
                
                            action checkNonRedundancy : SupervisionStep {
                                ::> description =
                                    "Checks that the new requirement is not redundant based on the current list of accepted requirements";
                                agent = checkNonRedundancyAgent;
                                in newRequirement : String;
                                in systemDescription : String;
                                in requirements : String [0..*];
                                out isNonRedundant : Boolean;
                            }
                            then joinNode;
                
                            action checkSatisfiability : SupervisionStep {
                                ::> description = "Checks whether the new requirement is satisfiable.";
                                ::> agent = checkSatisfiabilityAgent;
                                in newRequirement : String;
                                in systemDescription : String;
                                in requirements : String [0..*];
                                out isSatisfiable : Boolean;
                            }
                            then joinNode;
                
                            then join joinNode;
                
                            then action checkConsensus : ConsensusStep {
                                ::> agent = consensusAgent;
                                in isCorrect : Boolean;
                                in isNonRedundant : Boolean;
                                in isSatisfiable : Boolean;
                                out allValid = isCorrect and isNonRedundant and isSatisfiable;
                            }
                
                            then action updateRequirements : Step {
                                ::> description =
                                    "Updates the list of accepted requirements and checks whether the list of accepted requirements is complete.";
                                ::> agent = requirementManagerAgent;
                                in requirements : String [0..*];
                                in newRequirement : String;
                                in systemDescription : String;
                                out isComplete : Boolean;
                            }
                        } until constructRequirementList.updateRequirements.isComplete;
                
                        then action outputToUser : Step {
                            ::> description = "Returns the list of completed requirements to the user.";
                            ::> agent = referenceAgent;
                            inout requirements : String [0..*];
                        }
                
                        then done;
                    }
                }
                
                """;

        var collabAgent = new CollaborationAgent();
        collabAgent.run(collaborationPattern);
    }
}
