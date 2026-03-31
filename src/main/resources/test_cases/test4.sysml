package RequirementManagementAgentsNoParams {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    action def GenerateRequirementsProcess {
        first start;

        then action updateRequirements : AgentStep {
                :>> description =
                    "Updates the list of accepted requirements and checks whether the list of accepted requirements is complete.";
                out isComplete : Boolean;
            }

    if updateRequirements.isComplete then outputToUser;
    else updateRequirements;



        action outputToUser : AgentStep {
            :>> description = "Returns the list of completed requirements to the user.";
        }

        then done;
    }
}
