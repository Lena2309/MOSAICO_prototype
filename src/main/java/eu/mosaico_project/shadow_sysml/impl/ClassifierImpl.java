package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Type;
import org.omg.sysml.lang.sysml.Classifier;

public class ClassifierImpl extends ElementImpl implements Element, Type {
    final String typeName ;

    public ClassifierImpl(Classifier c) {
        this.typeName = c.getDeclaredName();
        super(c);
    }

    @Override
    public String toString() {
        return "CLASSIFIER "  + this.typeName ;
    }
}
