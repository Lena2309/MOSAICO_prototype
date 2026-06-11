package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import org.omg.sysml.lang.sysml.Association;

public class AssociationImpl extends ElementImpl implements Element {
    @Override
    public String toString() {
        return "ASSOC "+ declaredName;
    }

    public AssociationImpl(Association a) {
        super(a);
    }
}
