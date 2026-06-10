package eu.mosaico_project.shadow_sysml.expression;

public class BooleanExpression implements Expression {
    BooleanExpression(org.omg.sysml.lang.sysml.BooleanExpression b){}

    @Override
    public String getDeclaredName() {
        return "NO NAME";
    }
}
