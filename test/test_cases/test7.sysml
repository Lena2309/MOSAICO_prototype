// Test case : prefix in dot notation

package TestPrefix2 {
    private import ScalarValues::*;
    private import SequenceFunctions::*;
    private import MOSAICO::*;

    part def EvaluatorAgent specializes MockFalseAgent {
         :>> id = "eval1" ;
    }
    part def OtherEvaluatorAgent specializes MockTrueAgent {
         :>> id = "eval2" ;
    }
    part def AnotherEvaluatorAgent specializes MockTrueAgent {
         :>> id = "eval3" ;
    }

    referenceAgent : ReferenceAgent;
    evaluatorAgent : EvaluatorAgent;
    otherEvaluatorAgent : OtherEvaluatorAgent;
    anotherEvaluatorAgent : AnotherEvaluatorAgent;

    action def Process {

        action setTestPassed : AgentStep {
            :>> description = "Test OK";
            :>> agent = referenceAgent;
            in result : Boolean ;
        }

        action setTestFailed : AgentStep {
            :>> description = "Test KO";
            :>> agent = referenceAgent;
            in result : Boolean ;
        }

        first start;

        then action anotherEvaluation : AgentStep {
             :>> description = "Check something unimportant." ;
             :>> agent = otherEvaluatorAgent ;
             out result : Boolean;
         }

        then action evaluation : AgentStep {
                :>> description = "Check something important." ;
                :>> agent = evaluatorAgent ;
                out result : Boolean;
            }

        then action otherEvaluation : AgentStep {
             :>> description = "Check something else, unimportant." ;
             :>> agent = otherEvaluatorAgent ;
             out result : Boolean;
         }

        if evaluation.result then setTestFailed;
        else setTestPassed;

        then done;
    }
}
