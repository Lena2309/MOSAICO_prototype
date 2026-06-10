package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.expression.Expression;

import java.util.List;


public class TransitionUsageImpl extends ElementImpl implements Element {
    final List<Expression> guards;
    final List<Element> following;
    public TransitionUsageImpl(org.omg.sysml.lang.sysml.TransitionUsage t) {
        super(t);
        this.guards = Simplifier.simplifyExpressionList(t.getGuardExpression());
        this.following = Simplifier.simplifyElementList(t.getEffectAction()); // FIXME
    }
}
