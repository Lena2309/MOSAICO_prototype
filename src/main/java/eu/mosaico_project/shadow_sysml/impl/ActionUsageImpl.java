package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.ActionUsage;
import eu.mosaico_project.shadow_sysml.Simplifier;

public class ActionUsageImpl extends OccurenceUsageImpl implements ActionUsage {


    public ActionUsageImpl(org.omg.sysml.lang.sysml.ActionUsage u) {
        super(u);
        var elements = u.getOwnedRelationship();
        var m = elements.stream().map(Simplifier::simplifyElement).toList();
        System.out.println("[FIXME] Content for ActionUsage.");
    }

    @Override
    public String toString(){
        return "ACTION  " + this.getDeclaredName() ;
    }
}
