package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.AttributeUsage;
import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;

import java.util.List;

public class AttributeUsageImpl extends UsageImpl implements AttributeUsage {

    final List<Element> other;

    public AttributeUsageImpl(org.omg.sysml.lang.sysml.AttributeUsage u) {
        super(u);
        this.other = Simplifier.simplifyRelationshipList(u.getOwnedRelationship());
    }

    @Override
    public String toString() {
        return "ATTRIBUTE " + this.getDeclaredName();
    }
}
