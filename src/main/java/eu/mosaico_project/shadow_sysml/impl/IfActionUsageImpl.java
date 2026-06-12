package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Feature;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.expression.Expression;

public class IfActionUsageImpl extends ElementImpl implements Feature {

    final Expression condition;
    final Element thenAction; // fixme : always ActionUsage
    final Element elseAction; // fixme : always ActionUsage

    public IfActionUsageImpl(org.omg.sysml.lang.sysml.IfActionUsage u) {
        super(u);
        this.condition = Simplifier.simplifyExpression(u.getIfArgument());
        this.thenAction = Simplifier.simplifyFeature(u.getThenAction());
        this.elseAction = Simplifier.simplifyFeature(u.getElseAction());
    }
}
