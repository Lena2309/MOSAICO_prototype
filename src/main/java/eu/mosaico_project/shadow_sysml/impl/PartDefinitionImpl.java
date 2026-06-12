package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.ItemDefinition;
import eu.mosaico_project.shadow_sysml.Type;

public class PartDefinitionImpl extends ItemDefinitionImpl implements ItemDefinition, Type {
    public PartDefinitionImpl(org.omg.sysml.lang.sysml.PartDefinition p) {
        super(p);
    }

    @Override
    public String toString() {
        return "PART DEF " + definedName;
    }
}
