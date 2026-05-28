package IntermediateAssignment {
    private import ScalarValues::*;
    private import MOSAICO::*;
    private import SequenceFunctions::*;

    action def TestAssignment {
        attribute requirements : String[0..*] ;

        first start;

        assign requirements := () ;

        then assign requirements := requirements->including("a"); // "including" comes from "SequenceFunctions" imported above
        then assign requirements := requirements->including("b");

        then done;
    }
}