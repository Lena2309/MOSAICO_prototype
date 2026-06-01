package SimpleAssignment {
    private import ScalarValues::*;
    private import MOSAICO::*;
    private import SequenceFunctions::*;

    action def TestAssignment {

        attribute counter : Boolean ;
        first start;
        assign counter := false;
        then assign counter := true;
        out chan = counter ;
    }
}