package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import org.omg.sysml.lang.sysml.Classifier;

public class ClassifierImpl extends ElementImpl implements Element {
    public ClassifierImpl(Classifier c) {
        super(c);
    }

    @Override
    public String toString() {
        return "CLASSIFIER " + declaredName ;
    }
}
