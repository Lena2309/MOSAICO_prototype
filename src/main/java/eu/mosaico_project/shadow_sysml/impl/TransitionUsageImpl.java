package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Feature;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.Usage;
import eu.mosaico_project.shadow_sysml.expression.Expression;

import java.security.InvalidParameterException;
import java.util.List;


public class TransitionUsageImpl extends ElementImpl implements Usage {
    final List<Expression> guards;
    final Feature following;
    final Feature target;

    /** Warning, in SysML AST, a TransitionUsage is a both a Feature and a Relationship.
     It references nodes that are available by other means (--> risk of cycle) */
    public TransitionUsageImpl(org.omg.sysml.lang.sysml.TransitionUsage t) {
        super(t);
        this.guards = Simplifier.simplifyExpressionList(t.getGuardExpression());
        this.following = Simplifier.simplifyFeature(t.getSuccession());
        this.target = Simplifier.simplifyFeature(t.getTarget());
        if (!t.getTriggerAction().isEmpty())
            throw new InvalidParameterException("Missed information");
        if (!t.getEffectAction().isEmpty())
            throw new InvalidParameterException("Missed information");

    }

    @Override
    public String toString() {
        return "GOTO " + (this.guards.isEmpty() ? "(ELSE)" : "(IF/THEN)") + " " + target ;
    }
}
