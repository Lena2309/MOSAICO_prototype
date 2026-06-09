package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Definition;
import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;

import java.util.List;

public class DefinitionImpl extends ElementImpl implements Definition {
    final List<Element> definitionMembers;
    public DefinitionImpl(org.omg.sysml.lang.sysml.Definition d) {
        super(d);
        // FIXME : duplicated code with NameSpaceImpl

        /* Use getOwnedMember instead of OwnedMember or you will get all the imported elements. */
        this.definitionMembers = d.getOwnedMember().stream()./*filter(this::keep).*/map(Simplifier::simplifyElement).toList();

    }

    /*public boolean keep(org.omg.sysml.lang.sysml.Element e){
        // We don't handle references to self to avoid non-termination.
        String n = e.getDeclaredName();
        boolean c1 = !"self".equals(n);
        boolean c2 = this.declaredName != null && !this.declaredName.equals(n);
        return c1 && c2; // declared name can be null.
    }*/

}
