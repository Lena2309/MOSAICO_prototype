package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Relationship;
import eu.mosaico_project.shadow_sysml.Simplifier;
import org.omg.sysml.lang.sysml.Membership;

public class MembershipImpl extends ElementImpl implements Relationship {

    final Element element ;

    public MembershipImpl(Membership m) {
        super(m);
        this.element = Simplifier.simplifyElement(m.getMemberElement());
    }
}
