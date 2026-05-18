package SimpleAssignment {
    private import ScalarValues::*;
    private import MOSAICO::*;
    private import SequenceFunctions::*;

    action def TestAssignment {

        attribute loopIteration : Integer ;

        first start;

        assign loopIteration := 1;



        then action setTrue {
            assign loopIteration := 2;
            }

        then done;
    }
}