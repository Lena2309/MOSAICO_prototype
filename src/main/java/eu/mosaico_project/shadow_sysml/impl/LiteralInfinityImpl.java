package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.expression.Expression;

public class LiteralInfinityImpl implements Expression {

    public LiteralInfinityImpl(org.omg.sysml.lang.sysml.LiteralInfinity i){

    }

    @Override
    public String toString(){
        return "\"* (infinity)\"";
    }

}
