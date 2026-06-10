package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.ActionUsage;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.Element;


import java.util.List;

public class ActionUsageImpl extends OccurenceUsageImpl implements ActionUsage {

    final List<Element> actionMembers ;

    public ActionUsageImpl(org.omg.sysml.lang.sysml.ActionUsage u) {
        super(u);
        List<org.omg.sysml.lang.sysml.Element> ms = u.getOwnedMember();
        this.actionMembers = Simplifier.simplifyElementList(ms);
    }

    @Override
    public String toString(){
        return "ACTION  " + this.getDeclaredName() ;
    }
}
