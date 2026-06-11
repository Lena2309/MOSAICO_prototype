package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;

import java.util.List;


public class FeatureImpl extends ElementImpl implements Element {
    List<Element> types;
    public FeatureImpl(org.omg.sysml.lang.sysml.Feature f) {
        super(f);
        this.types = Simplifier.simplifyElementList(f.getType());
    }
}
