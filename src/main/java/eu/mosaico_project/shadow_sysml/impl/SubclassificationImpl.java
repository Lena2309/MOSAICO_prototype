package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import org.omg.sysml.lang.sysml.Subclassification;

import java.util.List;

public class SubclassificationImpl extends ElementImpl implements Element {

    final List<Element> superClasses ;

    @Override
    public String toString() {
        return "SUBCLASS";
    }

    public SubclassificationImpl(Subclassification st) {
        super(st);
        this.superClasses = Simplifier.simplifyElementList(st.getTarget());
    }
}
