// Test case : behavior of value 'true' in 'if' condition

package RequirementManagementAgentsNoParams {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    part def CompletionEvaluatorAgent specializes MockFalseAgent {
         :>> id = "eu.mosaico-project.check-cp-eval-agent-type.v1";
    }

    referenceAgent : ReferenceAgent;
    evaluatorAgent : CompletionEvaluatorAgent;

    action def GenerateRequirementsProcess {
        action outputOKToUser : AgentStep {
            :>> description = "OK";
            :>> agent = referenceAgent;
            out userOut : String ;
        }

        action outputKOToUser : AgentStep {
            :>> description = "KO";
            :>> agent = referenceAgent;
            out userOut : String ;
        }

        first start;

        then action updateRequirements : AgentStep {
                :>> description =
                    "Updates the list of accepted requirements and checks whether the list of accepted requirements is complete.";
                :>> agent = evaluatorAgent ;
                out isComplete : Boolean;
            }

        if updateRequirements.isComplete then outputOKToUser;
        else outputKOToUser;

        then done;
    }
}
