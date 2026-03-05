private import ScalarValues::*;
private import Collections::*;

package MOSAICO {

    part def Agent {
        attribute id : String[0..1];
        attribute version : String[0..1];
        attribute licence : String;
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

    action def Step {
        attribute description : String[0..1];
        ref agent: Agent[0..1];
    }

    action def ReferenceStep specializes Step {
        redefines agent : ReferenceAgent;
    }
    
    action def ConsensusStep specializes Step {
        redefines agent : ConsensusAgent;
        attribute policy : String[0..1];
    }

    action def SupervisionStep specializes Step {
        redefines agent : SupervisionAgent;
    }

}
