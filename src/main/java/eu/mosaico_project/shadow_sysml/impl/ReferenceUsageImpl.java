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

    final String usageOf ;
    final List<Type> typeClassifiers = new ArrayList<>();
    final List<Element> redefinitions = new ArrayList<>();
    final List<Expression> features = new ArrayList<>();
    final List<Feature> members = new ArrayList<>();
    final List<Feature> fixme = new ArrayList<>();
    final List<Element> ownedElements = new ArrayList<>();



    @Nullable
    FeatureDirectionKind direction ;

    public ReferenceUsageImpl(org.omg.sysml.lang.sysml.ReferenceUsage r) {
        super(r);

        EList<Classifier> tmp = r.getDefinition();
        if (tmp.size() != 1)
            throw new InvalidParameterException("[REFERENCE USAGE] Bad number of references.");
        else
            this.usageOf = tmp.getFirst().getDeclaredName(); // Since this is a REFERENCE, we don't want to get the whole referenced object

        for (org.omg.sysml.lang.sysml.Relationship rel : r.getOwnedRelationship()){
            switch (rel) {
                case org.omg.sysml.lang.sysml.Redefinition d -> this.redefinitions.add(new FixMeElement(d.path()));
                case org.omg.sysml.lang.sysml.FeatureValue v -> this.features.add(Simplifier.simplifyExpression(v.getValue()));
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
        return "USAGE (REF) " + this.usageOf + " " + (this.direction!= null ? "(" + this.direction + ")": "");
    }
}
