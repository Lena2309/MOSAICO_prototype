package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.AttributeUsage;
import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.Type;


import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class AttributeUsageImpl extends UsageImpl implements AttributeUsage {

    final String attributeName;

    final List<Type> types = new ArrayList<>();
    final List<Element> ownedMembers = new ArrayList<>();
    //final List<MultiplicityRangeImpl> multiplicity = new ArrayList<>();
    final List<Element> redefinitions = new ArrayList<>();

    public AttributeUsageImpl(org.omg.sysml.lang.sysml.AttributeUsage u) {
        super(u);
        this.attributeName = u.getDeclaredName();
        for (org.omg.sysml.lang.sysml.Relationship r : u.getOwnedRelationship()){
            switch (r){
                case org.omg.sysml.lang.sysml.FeatureTyping ft -> this.types.add(Simplifier.simplifyType(ft.getType()));
                case org.omg.sysml.lang.sysml.OwningMembership m -> this.ownedMembers.add(Simplifier.simplifyElement(m.getOwnedMemberElement()));
                case org.omg.sysml.lang.sysml.Redefinition d -> this.redefinitions.add(Simplifier.simplifyFeature(d.getRedefinedFeature()));
                default ->
                        throw new InvalidParameterException("[ATTRIBUTE USAGE] " + r.getClass().getSimpleName());
            }
        }

    }

    @Override
    public String toString() {
        return "ATTRIBUTE " + this.attributeName;
    }
}
