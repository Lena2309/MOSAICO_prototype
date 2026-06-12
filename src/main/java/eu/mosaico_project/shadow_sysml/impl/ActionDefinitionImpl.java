package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.ActionDefinition;
import eu.mosaico_project.shadow_sysml.Type;

public class ActionDefinitionImpl extends OccurenceDefinitionImpl implements ActionDefinition, Type {
    public ActionDefinitionImpl(org.omg.sysml.lang.sysml.ActionDefinition d) {
        super(d);
    }

    @Override
    public String toString(){
        return "ACTION DEF " + this.definedName ;
    }
}
