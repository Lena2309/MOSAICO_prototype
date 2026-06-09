package eu.mosaico_project.shadow_sysml.impl;

public class ElementImpl implements eu.mosaico_project.shadow_sysml.Element {

    final String declaredName;

    public ElementImpl(org.omg.sysml.lang.sysml.Element e){
        this.declaredName = e.getDeclaredName();
    }

    @Override
    public String getDeclaredName() {
        return this.declaredName;
    }
}
