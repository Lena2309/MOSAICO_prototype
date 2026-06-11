package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.SuccessionAsUsage;

/** Represents the keyword 'then'.*/
public class SuccessionAsUsageImpl extends ElementImpl implements SuccessionAsUsage {

    final String tmp ;

    public SuccessionAsUsageImpl(org.omg.sysml.lang.sysml.SuccessionAsUsage e) {
        super(e);
        this.tmp = e.path();
    }

    @Override
    public String toString(){
        return "THEN: " + this.tmp;
    }

}
