package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.expression.Expression;

public class LiteralIntegerImpl implements Expression {
    final int value;
    public LiteralIntegerImpl(org.omg.sysml.lang.sysml.LiteralInteger i){
        this.value = i.getValue();
    }

    @Override
    public String toString(){
        return "\"" + this.value + "\"";
    }

}
