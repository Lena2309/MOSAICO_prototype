package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Definition;
import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;


import java.util.List;

public class DefinitionImpl extends ElementImpl implements Definition {

    final List<Element> defMembers;

    public DefinitionImpl(org.omg.sysml.lang.sysml.Definition d) {
        super(d);
        this.defMembers = Simplifier.simplifyElementList(d.getOwnedMember());
    }



}
