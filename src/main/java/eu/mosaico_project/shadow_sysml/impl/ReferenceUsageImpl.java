package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.*;
import eu.mosaico_project.shadow_sysml.expression.Expression;
import org.eclipse.emf.common.util.EList;
import org.jspecify.annotations.Nullable;
import org.omg.sysml.lang.sysml.Classifier;
import org.omg.sysml.lang.sysml.FeatureDirectionKind;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ReferenceUsageImpl extends ElementImpl implements ReferenceUsage, Feature {

    final String name;

    final String usageOf ;
    final List<Type> typeClassifiers = new ArrayList<>();
    final List<String> redefinitions = new ArrayList<>();
    final List<Expression> values = new ArrayList<>();
    final List<Feature> members = new ArrayList<>();
    final List<Feature> fixme = new ArrayList<>();
    final List<Element> ownedElements = new ArrayList<>();

    @Nullable
    FeatureDirectionKind direction ;

    public ReferenceUsageImpl(org.omg.sysml.lang.sysml.ReferenceUsage r) {
        super(r);
        this.name = r.getDeclaredName();

        EList<Classifier> tmp = r.getDefinition();
        if (tmp.size() != 1)
            throw new InvalidParameterException("[REFERENCE USAGE] Bad number of references.");
        else
            this.usageOf = tmp.getFirst().getDeclaredName(); // Since this is a REFERENCE, we don't want to get the whole referenced object

        for (org.omg.sysml.lang.sysml.Relationship rel : r.getOwnedRelationship()){
            switch (rel) {

                case org.omg.sysml.lang.sysml.Redefinition d ->{
                    org.omg.sysml.lang.sysml.Feature f = d.getRedefinedFeature();
                    if (f.eIsProxy())
                        if (f.eContainer()!=null)
                            this.redefinitions.add(f.eContainer().toString());
                        else {
                            System.err.println("[WARNING] Unresolved reference (proxy).");
                            this.redefinitions.add("PROXY " + ((org.omg.sysml.lang.sysml.impl.FeatureImpl) f).eProxyURI());
                        }
                    else {
                        String id = f.getDeclaredName();
                        this.redefinitions.add(id != null ? id : f.path());
                    }
                }

                case org.omg.sysml.lang.sysml.FeatureValue v -> this.values.add(Simplifier.simplifyExpression(v.getValue()));
                case org.omg.sysml.lang.sysml.FeatureTyping t -> this.typeClassifiers.add(Simplifier.simplifyType(t.getType()));
                case org.omg.sysml.lang.sysml.FeatureMembership m -> this.members.add(Simplifier.simplifyFeature(m.getOwnedMemberFeature()));
                case org.omg.sysml.lang.sysml.ReferenceSubsetting s ->
                        this.fixme.add(Simplifier.simplifyFeature(s.getReferencedFeature()));
                case org.omg.sysml.lang.sysml.OwningMembership m ->
                        this.ownedElements.add(Simplifier.simplifyElement(m.getOwnedMemberElement()));
             default -> throw new InvalidParameterException("[REFERENCE USAGE] " + rel.getClass().getSimpleName());
            }

        }


        if (r.getDirection() != null)  this.direction = r.getDirection();
    }


    @Override
    public String toString() {
        return "USAGE (REF) " + name + " " + this.usageOf + " " + (this.direction!= null ? "(" + this.direction + ")": "")  ;
    }


    enum Kind { REDEFINITION, TYPING_DECLARATION, FIXME }

    public static Kind classify(org.omg.sysml.lang.sysml.ReferenceUsage r){
        if (!r.getOwnedRedefinition().isEmpty()) return Kind.REDEFINITION ;
        else
            if (r.getDeclaredName() != null && r.getDefinition().getFirst().getDeclaredName() != null && !r.getType().isEmpty())
                return Kind.TYPING_DECLARATION ;
            else return Kind.FIXME;
    }

}
