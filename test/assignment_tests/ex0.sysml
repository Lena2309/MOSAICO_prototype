package SimpleAssignment {
    private import ScalarValues::*;
    private import MOSAICO::*;
    private import SequenceFunctions::*;

    action def TestAssignment {

        attribute counter : Integer ;

        first start;

        assign counter := 0;

        then action setTrue {
            assign counter := counter + 1;
            }

        then done;
    }
}