package org.example.dto;

import org.example.dto.conditional.expression.Expression ;

public class Assignment implements Statement{
    String lvalue ;
    Expression rvalue ;

    public Assignment(String lvalue, Expression rvalue) {
        this.lvalue = lvalue;
        this.rvalue = rvalue ;
    }

    public void execute(State s){
        var result = rvalue.eval(s);
        s.write(lvalue, result);
    };
}
