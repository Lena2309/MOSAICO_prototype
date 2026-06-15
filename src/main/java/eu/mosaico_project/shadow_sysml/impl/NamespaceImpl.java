package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/** Keyword 'specializes' in source code. */
public class NamespaceImpl extends ElementImpl {

    final String definedName;
    final List<Element> ownedMembers = new ArrayList<>();
    final List<Usage> usages = new ArrayList<>();
    final List<Definition> definitions = new ArrayList<>();
    final List<Feature> otherFeatures = new ArrayList<>();
    final List<String> superTypes= new ArrayList<>();

    public NamespaceImpl(org.omg.sysml.lang.sysml.Namespace e) {
        super(e);
        this.definedName = e.getDeclaredName();

        for (org.omg.sysml.lang.sysml.Relationship r : e.getOwnedRelationship()){
            // Classify the nature of the relationship.
            switch (r)  {
                // Warning : relationships refer to possibly already handled elements
                case org.omg.sysml.lang.sysml.Subclassification s -> {
                    this.superTypes.add(s.getSuperclassifier().getDeclaredName());
                }
                case org.omg.sysml.lang.sysml.OwningMembership m ->
                        this.classifyMember(Simplifier.simplifyElement(m.getOwnedMemberElement()));
                case org.omg.sysml.lang.sysml.Membership m ->
                        this.classifyMember(Simplifier.simplifyElement(m.getMemberElement()));
                case org.omg.sysml.lang.sysml.NamespaceImport i ->
                        discard(i);
                case org.omg.sysml.lang.sysml.MembershipImport i ->
                        discard(i);
                default ->
                        throw new InvalidParameterException("[NAMESPACE] Not supported: " + r.getClass().getSimpleName());
            }
        }
    }

    void classifyMember(Element e){
        switch (e) {
            case Usage u ->
                    this.usages.add(u);
            case Definition d ->
                    this.definitions.add(d);
            case Feature f ->
                    this.otherFeatures.add(f);
            default ->
                    this.ownedMembers.add(e);
        }
    }




}
