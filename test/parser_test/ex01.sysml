package TestPack {
    private import ScalarValues::*;

    action TestAction {
        attribute counter : Integer ;
        first start;
        then assign counter := 1;
        then done;
    }
}
