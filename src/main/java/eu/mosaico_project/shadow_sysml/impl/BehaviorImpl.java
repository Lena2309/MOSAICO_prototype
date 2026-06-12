package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Behavior;
import eu.mosaico_project.shadow_sysml.Type;

public class BehaviorImpl extends ElementImpl implements Behavior, Type {
    public BehaviorImpl(org.omg.sysml.lang.sysml.Behavior b) {
        super(b);
    }
}
