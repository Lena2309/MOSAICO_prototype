package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;

public class FixMeElement implements Element {
    final String about ;
    @Deprecated
    public FixMeElement(String comment){
        this.about = comment;
    }
}
