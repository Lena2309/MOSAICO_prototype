package org.example.dto;


import org.omg.sysml.lang.sysml.Expression;

public class ExpressionBuilder {
    public static org.example.dto.Expression transpile(org.omg.sysml.lang.sysml.Expression e){
        return new IdentifierExpression("isComplete");
    }
}
