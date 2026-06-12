package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.expression.Expression;

public class LiteralStringImpl implements Expression {
    final String value;
    public LiteralStringImpl(org.omg.sysml.lang.sysml.LiteralString s){
        this.value = s.getValue();
    }

    @Override
    public String toString(){
        return "\"" + this.value + "\"";
    }


}
