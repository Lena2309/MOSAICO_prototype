package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.ActionUsage;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.Element;
import org.omg.sysml.lang.sysml.*;


import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;


public class ActionUsageImpl extends OccurenceUsageImpl implements ActionUsage {

    final String actionName ;
    final List<Element> redefinitions  = new ArrayList<>();
    final List<Element> members  = new ArrayList<>();
    final List<Element> features  = new ArrayList<>();

    final List<String> declaredTypes  = new ArrayList<>();
    final List<Element> fixme = new ArrayList<>();

    public ActionUsageImpl(org.omg.sysml.lang.sysml.ActionUsage u) {
        super(u);
        this.actionName = u.getDeclaredName();

        for (Relationship r : u.getOwnedRelationship()){
            switch (r) {
                case FeatureTyping t -> declaredTypes.add(t.getType().getDeclaredName()); //types.add(Simplifier.simplifyElement(t.getType()));
                case FeatureMembership m -> features.add(Simplifier.simplifyFeature(m.getOwnedMemberFeature()));
                case Redefinition d -> redefinitions.add(new FixMeElement(d.path()));
                case OwningMembership m -> members.add(Simplifier.simplifyElement(m.getOwnedMemberElement()));
                case Membership m -> members.addAll(Simplifier.simplifyElementList(m.getTarget()));
                case Subsetting s -> this.fixme.add(new FixMeElement(s.path()));
                default ->
                        throw new InvalidParameterException("[ACTION_USAGE] Not supported: " + r.getClass().getSimpleName());
            }
        }
    }

    @Override
    public String toString(){
        return "ACTION  " + this.actionName ;
    }
}
