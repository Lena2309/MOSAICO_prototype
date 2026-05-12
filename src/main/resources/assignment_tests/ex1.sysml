package SimpleAssignment {
    private import ScalarValues::*;
    private import MOSAICO::*;
    private import SequenceFunctions::*;

    action def TestAssignment {
        attribute loopIteration : Integer := 0;
        first start;

        then loop action printNumbers {
            assign loopIteration := loopIteration + 1;

            action printToUser : ReferenceStep {
                in loopIteration : Integer;
            }
        } until loopIteration > 50;

        then done;
    }
}