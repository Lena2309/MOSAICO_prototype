package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;


public class NamespaceImpl extends ElementImpl implements Namespace {

    final String definedName;
    final List<Element> ownedMembers = new ArrayList<>();
    final List<Feature> usages = new ArrayList<>();
    final List<Element> definitions = new ArrayList<>();
    final List<Feature> unclassifiedFeatures = new ArrayList<>();
    final List<String> superTypes= new ArrayList<>();
    final List<Element> declarations = new ArrayList<>();
    final List<Element> redefinitions = new ArrayList<>();
    final List<Feature> attributes = new ArrayList<>();
    final List<Element> flow = new ArrayList<>();

    public NamespaceImpl(org.omg.sysml.lang.sysml.Namespace e) {
        super(e);
        this.definedName = e.getDeclaredName();

        for (org.omg.sysml.lang.sysml.Relationship r : e.getOwnedRelationship()){
            // Classify the nature of the relationship.
            switch (r)  {
                // Warning : relationships refer to possibly already handled elements
                case org.omg.sysml.lang.sysml.Subclassification s -> {
                    /* Keyword 'specializes' in source code. */
                    this.superTypes.add(s.getSuperclassifier().getDeclaredName());
                }
                case org.omg.sysml.lang.sysml.FeatureMembership m->
                        this.classifyMember(m.getOwnedMemberFeature());
                case org.omg.sysml.lang.sysml.OwningMembership m ->
                        this.classifyMember(m.getOwnedMemberElement());
                case org.omg.sysml.lang.sysml.Membership m ->
                        this.classifyMember(m.getMemberElement()); // make a difference between member and owned member
                case org.omg.sysml.lang.sysml.NamespaceImport i ->
                        discard(i);
                case org.omg.sysml.lang.sysml.MembershipImport i ->
                        discard(i);
                default ->
                        throw new InvalidParameterException("[NAMESPACE] Not supported: " + r.getClass().getSimpleName());
            }
        }
    }

    void classifyMember(org.omg.sysml.lang.sysml.Element e){
        switch (e) {
            case org.omg.sysml.lang.sysml.ReferenceUsage r -> {
                switch (ReferenceUsageImpl.classify(r)){
                    case REDEFINITION -> this.redefinitions.add(Simplifier.simplifyFeature(r) );
                    case TYPING_DECLARATION -> this.declarations.add(Simplifier.simplifyFeature(r));
                    case FIXME -> this.usages.add(Simplifier.simplifyFeature(r));
                }
            }

            case org.omg.sysml.lang.sysml.AttributeUsage a ->
                this.attributes.add(Simplifier.simplifyFeature(a));

            case org.omg.sysml.lang.sysml.SuccessionAsUsage s ->
                    this.flow.add(Simplifier.simplifyFeature(s));
            case org.omg.sysml.lang.sysml.TransitionUsage t ->
                    this.flow.add(Simplifier.simplifyFeature(t));

            case org.omg.sysml.lang.sysml.Usage u ->{
                this.usages.add(Simplifier.simplifyFeature(u));
            }
            case org.omg.sysml.lang.sysml.Definition d ->
                    this.definitions.add(Simplifier.simplifyElement(d));



            case Feature f -> {
                    System.err.println("[FIXME] Unclassified Feature.");
                    this.unclassifiedFeatures.add(f);
            }
            default ->
                    this.ownedMembers.add(Simplifier.simplifyElement(e));
        }
    }

    @Override
    public List<String> getSuperTypes(){
        return this.superTypes;
    }

}
