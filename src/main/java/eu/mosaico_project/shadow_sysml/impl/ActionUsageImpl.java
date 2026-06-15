package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.*;
import eu.mosaico_project.shadow_sysml.expression.Expression;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;


public class ActionUsageImpl extends OccurenceUsageImpl implements ActionUsage {

    final String actionName ;
    final List<Element> redefinitions  = new ArrayList<>();
    final List<Element> members  = new ArrayList<>();
    final List<Element> usages  = new ArrayList<>();
    final List<Expression> expressions = new ArrayList<>() ;

    final List<String> declaredTypes  = new ArrayList<>();
    final List<Element> fixme = new ArrayList<>();

    final String referenceToDefinition;
    final List<Feature> inputs = new ArrayList<>();
    final List<Feature> outputs = new ArrayList<>();

    public ActionUsageImpl(org.omg.sysml.lang.sysml.ActionUsage u) {
        super(u);


        List<org.omg.sysml.lang.sysml.Behavior> tmp = u.getActionDefinition();
        if (tmp.size()!=1)
            throw new InvalidParameterException("[ACTION USAGE] Bad number of referenced definitions: " + tmp.size());

        // Warning : loop if analysing too much content in the def
        this.referenceToDefinition = tmp.getFirst().getDeclaredName();

        this.actionName = u.getDeclaredName();

        for (org.omg.sysml.lang.sysml.Relationship r : u.getOwnedRelationship()){
            switch (r) {
                case org.omg.sysml.lang.sysml.FeatureTyping ft -> {
                    final org.omg.sysml.lang.sysml.Type t = ft.getType();
                    declaredTypes.add(t.getDeclaredName());
                    //types.add(Simplifier.simplifyElement(t)); // this would loop
                }
                case org.omg.sysml.lang.sysml.FeatureMembership m ->
                        this.classify(m.getOwnedMemberFeature());
                case org.omg.sysml.lang.sysml.Redefinition d ->
                        redefinitions.add(new FixMeElement(d.path()));
                case org.omg.sysml.lang.sysml.OwningMembership m ->
                        members.add(Simplifier.simplifyElement(m.getOwnedMemberElement()));
                case org.omg.sysml.lang.sysml.Membership m ->
                        members.addAll(Simplifier.simplifyElementList(m.getTarget()));
                case org.omg.sysml.lang.sysml.Subsetting s ->
                        this.fixme.add(new FixMeElement(s.path()));
                default ->
                        throw new InvalidParameterException("[ACTION_USAGE] Not supported: " + r.getClass().getSimpleName());
            }
        }

        for (org.omg.sysml.lang.sysml.Feature f : u.getInput()){
            this.inputs.add(Simplifier.simplifyFeature(f));
        }
        for (org.omg.sysml.lang.sysml.Feature f : u.getOutput()){
            this.outputs.add(Simplifier.simplifyFeature(f));
        }

    }

    private void classify(org.omg.sysml.lang.sysml.Feature f) {
        switch(f){
            case org.omg.sysml.lang.sysml.Usage u ->{
                    if (!u.getOwnedRedefinition().isEmpty())
                        this.redefinitions.add(Simplifier.simplifyFeature(u));
                    else
                        this.usages.add(Simplifier.simplifyFeature(u));
                    }
            case org.omg.sysml.lang.sysml.Expression e ->
                this.expressions.add(Simplifier.simplifyExpression(e));

            default -> throw new InvalidParameterException("[ACTION_USAGE][CLASSIFY] Not supported: " + f.getClass().getSimpleName());
        }

    }

    @Override
    public List<String> getDeclaredType(){
        return this.declaredTypes;
    };

    @Override
    public String toString(){
        return "ACTION  " + this.actionName ;
    }
}
