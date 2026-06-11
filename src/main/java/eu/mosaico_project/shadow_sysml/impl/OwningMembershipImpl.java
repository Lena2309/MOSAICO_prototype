package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import org.omg.sysml.lang.sysml.OwningMembership;

public class OwningMembershipImpl extends ElementImpl implements Element {
    final Element element;
    public OwningMembershipImpl(OwningMembership m) {
        super(m);
        this.element = Simplifier.simplifyElement(m.getMemberElement());
    }
}
