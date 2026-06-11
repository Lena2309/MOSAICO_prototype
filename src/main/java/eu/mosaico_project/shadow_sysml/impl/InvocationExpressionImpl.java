package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.expression.Expression;

import org.omg.sysml.lang.sysml.InvocationExpression;

import java.util.List;


public class InvocationExpressionImpl extends ElementImpl implements Expression {

    final Element fun ;
    final List<Expression> args ;

    public InvocationExpressionImpl(InvocationExpression i) {
        super(i);
        this.fun = Simplifier.simplifyElement(i.getFunction());
        this.args = Simplifier.simplifyExpressionList(i.getArgument());
        var c = i.eContents();
    }

    @Override
    public String toString() {
        return "INVOCATION " + fun ;
    }
}
