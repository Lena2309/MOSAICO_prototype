package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Function;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.Type;
import eu.mosaico_project.shadow_sysml.expression.Expression;

import java.util.List;

public class FunctionImpl extends ElementImpl implements Function, Type {

    final String functionName ;
    final List<Expression> steps;

    public FunctionImpl(org.omg.sysml.lang.sysml.Function f) {
        super(f);
        this.functionName = f.getDeclaredName();
        this.steps = f.getExpression().stream().map(Simplifier::simplifyExpression).toList();
    }

    @Override
    public String toString() {
        return "FUNCTION " + functionName ;
    }
}
