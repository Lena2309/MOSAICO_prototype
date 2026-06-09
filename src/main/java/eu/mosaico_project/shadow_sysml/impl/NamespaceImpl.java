package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;

import java.util.List;

public class NamespaceImpl extends ElementImpl {

    final List<Element> members;

    public NamespaceImpl(org.omg.sysml.lang.sysml.Namespace e) {
        super(e);
        this.members = e.getMember().stream().map(Simplifier::simplifyElement).toList();
    }
}
