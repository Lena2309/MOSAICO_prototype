package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.expression.Expression;

import java.util.List;

public class ConstraintUsageImpl extends ElementImpl implements Expression {
    List<Element> relations ;
    public ConstraintUsageImpl(org.omg.sysml.lang.sysml.ConstraintUsage e) {
        super(e);
        this.relations = Simplifier.simplifyElementList(e.getOwnedRelationship());
    }
}
