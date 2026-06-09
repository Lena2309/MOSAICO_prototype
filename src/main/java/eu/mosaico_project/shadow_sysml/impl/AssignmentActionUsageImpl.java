package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.AssignmentActionUsage;
import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.expression.Expression;

public class AssignmentActionUsageImpl extends ActionUsageImpl implements AssignmentActionUsage {
    final Expression rvalue;
    final Element lvalue;

    public AssignmentActionUsageImpl(org.omg.sysml.lang.sysml.AssignmentActionUsage u) {
        this.rvalue = Simplifier.simplifyExpression(u.getValueExpression());
        this.lvalue = Simplifier.simplifyElement(u.getReferent());
        // FIXME : what about getTargetArgument ?
        super(u);
    }

    @Override
    public String toString() {
        return "ASSIGN " + lvalue.getDeclaredName() + " = ...";
    }
}
