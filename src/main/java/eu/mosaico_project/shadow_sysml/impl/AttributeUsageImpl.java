package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.AttributeUsage;

public class AttributeUsageImpl extends UsageImpl implements AttributeUsage {
    public AttributeUsageImpl(org.omg.sysml.lang.sysml.AttributeUsage u) {
        super(u);
    }

    @Override
    public String toString() {
        return "ATTRIBUTE " + this.getDeclaredName();
    }
}
