package org.example.dto;

import org.example.dto.conditional.expression.Expression ;

public class Assignment implements Statement{
    String lvalue ;
    Expression rvalue ;

    public Assignment(String lvalue, Expression rvalue) {
        this.lvalue = lvalue;
        this.rvalue = rvalue ;
    }

    public void execute(ChannelState s, AttributeState memory){
        var result = rvalue.eval(s, memory);
        memory.put(lvalue, result);
    };
}
