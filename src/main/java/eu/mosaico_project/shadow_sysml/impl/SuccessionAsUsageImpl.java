package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.SuccessionAsUsage;

/** Represents the keyword 'then'.*/
public class SuccessionAsUsageImpl extends ElementImpl implements SuccessionAsUsage {
    public SuccessionAsUsageImpl(org.omg.sysml.lang.sysml.Element e) {
        super(e);
    }

    @Override
    public String toString(){return "THEN";}
}
