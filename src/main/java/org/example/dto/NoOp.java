package org.example.dto;

public class NoOp implements Statement {
    @Override
    public void execute(State s) {
        System.out.println("noop");
    }
}
