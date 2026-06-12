package eu.mosaico_project.shadow_sysml.impl;



import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Feature;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.Type;
import org.omg.sysml.lang.sysml.FeatureTyping;
import org.omg.sysml.lang.sysml.Subsetting;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;


public class FeatureImpl extends ElementImpl implements Feature {
    final String name;
    List<Type> types = new ArrayList<>();
    List<Element> ownedMembers = new ArrayList<>();
    List<Element> actions = new ArrayList<>();

    List<Element> fixme = new ArrayList<>();

    public FeatureImpl(org.omg.sysml.lang.sysml.Feature f) {
        super(f);
        this.name = f.getDeclaredName();
        for (org.omg.sysml.lang.sysml.Relationship r : f.getOwnedRelationship()){
            // Warning: Relationship connect elements that are available elsewhere in the AST
            switch (r) {
                case FeatureTyping t -> this.types.add(Simplifier.simplifyType(t.getType()));
                case org.omg.sysml.lang.sysml.OwningMembership m -> this.ownedMembers.add(Simplifier.simplifyElement(m.getOwnedMemberElement()));
                case Subsetting s -> this.fixme.add(new FixMeElement(s.path()));
                case org.omg.sysml.lang.sysml.Comment c -> Simplifier.discard(c);
                case org.omg.sysml.lang.sysml.FeatureChaining c -> this.fixme.add(new FixMeElement(c.path()));
                case org.omg.sysml.lang.sysml.ActionUsage a -> this.actions.addAll(Simplifier.simplifyElementList(a.getActionDefinition()));
             default ->
                     throw new InvalidParameterException("[FEATURE] " + r.getClass().getSimpleName());
            }

        }

    }
}
