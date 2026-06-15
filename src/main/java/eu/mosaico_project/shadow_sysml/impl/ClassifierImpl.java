package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Type;


public class ClassifierImpl extends ElementImpl implements Element, Type {
    final String typeName ;

    public ClassifierImpl(org.omg.sysml.lang.sysml.Classifier c) {
        super(c);
        this.typeName = c.getDeclaredName();
    }

    @Override
    public String toString() {
        return "CLASSIFIER "  + this.typeName ;
    }
}
