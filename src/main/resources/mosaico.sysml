package MOSAICO {

    private import ScalarValues::*;
    private import Collections::*;

    part def Agent {
        attribute id : String[0..1];
        attribute version : String[0..1];
        attribute licences : String[0..*];
        attribute skills : String[0..*];
    }

    part def ReferenceAgent specializes Agent {
        redefines id = "eu.mosaico-project.reference-agent-type";
        redefines version = "1.0";
    }

    part def ConsensusAgent specializes Agent {
        redefines id = "eu.mosaico-project.consensus-agent-type";
        redefines version = "1.0";
    }

    part def SolutionAgent specializes Agent;

    part def SupervisionAgent specializes Agent;

    part def MockTrueAgent specializes SolutionAgent {
        :>> id = "mock.true"
    }

    part def MockFalseAgent specializes SolutionAgent {
        :>> id = "mock.false"
    }

    part def MockStringAgent specializes SolutionAgent {
        :>> id = "mock.bla"
    }
    part def MockMultipleValueAgent specializes SolutionAgent {
            :>> id = "mock.multi"
    }

    action def Step {
        attribute description : String[0..1];
    }

    action def AgentStep specializes Step {
        ref agent : Agent[0..1];
    }

    action def ReferenceStep specializes AgentStep {
        redefines agent : ReferenceAgent;
    }

    action def ConsensusStep specializes AgentStep {
        redefines agent : ConsensusAgent;
        attribute policy : String[0..1];
    }

    action def SupervisionStep specializes AgentStep {
        redefines agent : SupervisionAgent;
    }

    action def ReplicationStep specializes AgentStep {
        ref agents: Agent[0..*];
        attribute repetitions : Integer[0..*];
        attribute prompts : String[0..*];
    }

}
