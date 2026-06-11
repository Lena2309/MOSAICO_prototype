package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.ActionUsage;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.Element;
import org.omg.sysml.lang.sysml.*;


import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ActionUsageImpl extends OccurenceUsageImpl implements ActionUsage {

    final List<Element> redefinitions ;
    final List<Element> members ;
    final List<Element> features ;

    final List<String> declaredTypes;

    public ActionUsageImpl(org.omg.sysml.lang.sysml.ActionUsage u) {
        super(u);

        this.declaredTypes = new ArrayList<>();
        this.members = new ArrayList<>();
        this.redefinitions = new ArrayList<>();
        this.features = new ArrayList<>();

        for (Relationship r : u.getOwnedRelationship()){
            switch (r) {
                case FeatureTyping t -> declaredTypes.add(t.getType().getDeclaredName()); //types.add(Simplifier.simplifyElement(t.getType()));
                case FeatureMembership m -> features.add(Simplifier.simplifyFeature(m.getOwnedMemberFeature()));
                case Redefinition d -> redefinitions.add(Simplifier.simplifyElement(d));
                case OwningMembership m -> members.add(Simplifier.simplifyElement(m.getOwnedMemberElement()));
                case Membership m -> members.addAll(Simplifier.simplifyElementList(m.getTarget()));
                default ->
                        throw new InvalidParameterException("[ACTION_USAGE] Not supported: " + r.getClass().getSimpleName());
            }
        }
    }

    @Override
    public String toString(){
        return "ACTION  " + this.getDeclaredName() ;
    }
}
