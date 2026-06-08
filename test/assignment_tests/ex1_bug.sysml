// BUG TO FIX

package SimpleAssignment {
    private import ScalarValues::*;
    private import MOSAICO::*;
    private import SequenceFunctions::*;

    action def TestAssignment {
        attribute loopIteration : Integer ;
        first start;

        assign loopIteration := 0 ;

        then loop action printNumbers {
            assign loopIteration := loopIteration + 1;
        } until false ;

        then done;
    }
}