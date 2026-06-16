package eu.mosaico_project.shadow_sysml.impl;

public class ElementImpl implements eu.mosaico_project.shadow_sysml.Element {

    public ElementImpl(org.omg.sysml.lang.sysml.Element e){

    }

    /** Elements that we don't want in simplified AST are discarded. */
    void discard(org.omg.sysml.lang.sysml.Element e){
        System.err.println("[WARNING] Discarded element :" + e.getClass().getSimpleName());
        // Nothing to do.
    }

}
