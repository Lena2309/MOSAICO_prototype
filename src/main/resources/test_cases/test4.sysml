// Test case : behavior of value 'true' in 'if' condition

package TestConditionTrue {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    part def EvaluatorAgent specializes MockTrueAgent {
         :>> id = "eval1" ;
    }

    referenceAgent : ReferenceAgent;
    evaluatorAgent : EvaluatorAgent;

    action def Process {

        action setTestPassed : AgentStep {
            :>> description = "OK";
            :>> agent = referenceAgent;
            out userOut : String ;
        }

        action setTestFailed : AgentStep {
            :>> description = "KO";
            :>> agent = referenceAgent;
            out userOut : String ;
        }

        first start;

        then action evaluation : AgentStep {
            :>> description = "Check something." ;
            :>> agent = evaluatorAgent ;
            out result : Boolean;
        }

        if evaluation.result then setTestPassed;
        else setTestFailed;

        then done;
    }
}
