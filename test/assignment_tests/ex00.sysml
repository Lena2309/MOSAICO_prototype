package SimpleAssignment {
    private import ScalarValues::*;
    private import MOSAICO::*;
    private import SequenceFunctions::*;

    action def TestAssignment {

        attribute loopIteration : Boolean := false;

        first start;

        //then action setTrue {
            // assign loopIteration := true;
            then assign loopIteration := true;
        //}

        then done;
    }
}