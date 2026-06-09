// Test case : send a message to a remote A2A agent and put its answer in a channel.
// Test with a plain A2A agent on port 8001 running https://github.com/a2aproject/a2a-samples/blob/main/samples/python/agents/number_guessing_game/agent_Alice.py
// Only the artifacts send by the remote A2A agents are taken into account (not the messages).

package TestConditionTrue {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    part def GeneratorAgent specializes RemoteAgent {
         :>> id = "foo" ;
         :>> url = "http://127.0.0.1:8001" ;
    }

    remoteAgent : GeneratorAgent;

    action def Process {

        first start;

        then action interact : AgentStep {
            :>> description = "5";
            :>> agent = remoteAgent;
            out results : String [0..*];
        }

        then done;
    }
}
