package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Definition;
import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import org.omg.sysml.lang.sysml.FeatureMembership;
import org.omg.sysml.lang.sysml.Membership;
import org.omg.sysml.lang.sysml.Relationship;
import org.omg.sysml.lang.sysml.Subclassification;


import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class DefinitionImpl extends ElementImpl implements Definition {


    final List<Element> superclasses ;
    final List<Element> features ;
    final List<Element> members ;

    public DefinitionImpl(org.omg.sysml.lang.sysml.Definition d) {
        super(d);

        superclasses = new ArrayList<>();
        features = new ArrayList<>();
        members = new ArrayList<>();

        for (Relationship r : d.getOwnedRelationship()){
            switch (r)  {
                case Subclassification s -> superclasses.addAll(Simplifier.simplifyElementList(s.getTarget()));
                case FeatureMembership m -> features.addAll(Simplifier.simplifyElementList(m.getTarget()));
                case Membership m -> members.addAll(Simplifier.simplifyElementList(m.getTarget()));
                default -> throw new InvalidParameterException("[DEFINITION] Not supported: " + r.getClass().getSimpleName());
            }
        }

        if (d.isVariation())
            System.out.println("[VAR]");

    }



}
