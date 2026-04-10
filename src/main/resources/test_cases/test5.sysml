// Test case : behavior of value 'true' in 'if' condition

package TestConditionFalse {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    part def EvaluatorAgent specializes MockFalseAgent {
         :>> id = "eval1" ;
    }

    referenceAgent : ReferenceAgent;
    evaluatorAgent : EvaluatorAgent;

    action def Process {

        action setTestPassed : AgentStep {
            :>> description = "Test OK";
            :>> agent = referenceAgent;
            out notUsed : String ;
        }

        action setTestFailed : AgentStep {
            :>> description = "Test KO";
            :>> agent = referenceAgent;
            out notUsed : String ;
        }

        first start;

        then action evaluation : AgentStep {
            :>> description = "Check something." ;
            :>> agent = evaluatorAgent ;
            out result : Boolean;
        }

        if evaluation.result then setTestFailed;
        else setTestPassed;

        then done;
    }
}
