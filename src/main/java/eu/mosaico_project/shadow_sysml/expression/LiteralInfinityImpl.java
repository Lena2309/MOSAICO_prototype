package eu.mosaico_project.shadow_sysml.expression;

public class LiteralInfinityImpl implements Expression {

    public LiteralInfinityImpl(org.omg.sysml.lang.sysml.LiteralInfinity i){

    }

    @Override
    public String toString(){
        return "\"* (infinity)\"";
    }

}
