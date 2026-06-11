package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import org.omg.sysml.lang.sysml.Feature;

public class RedefinitionImpl extends ElementImpl {

    Element looping, redefined;

    public RedefinitionImpl(org.omg.sysml.lang.sysml.Redefinition r) {
        // Redefinition <: Subsetting

        super(r);

        Feature f = r.getRedefiningFeature(); // ReferenceUsage

        var s2 = f.getOwnedRelationship(); // Comes from Element
        boolean flag_loop_in_ast =  (s2.getFirst() == r) ;

        //this.looping =Simplifier.simplifyFeature(r.getRedefiningFeature());

        this.redefined =Simplifier.simplifyFeature(r.getRedefinedFeature());
    }

    @Override
    public String toString() {
        return "REDEFINITION";
    }
}
