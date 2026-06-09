package SimpleAssignment {
    private import ScalarValues::*;
    private import MOSAICO::*;
    private import SequenceFunctions::*;

    action def TestAssignment {

        attribute counter : Integer ;
        attribute other : Integer ;

        first start;

        assign counter := 0;
        then assign other := 9 ;

        then action incr {
            first start ;
            then assign counter := counter + 1;
            then assign other := other - 1 ;
            }

        then done;
    }
}