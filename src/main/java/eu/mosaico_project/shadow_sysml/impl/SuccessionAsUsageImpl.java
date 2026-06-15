package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Feature;

import java.security.InvalidParameterException;


public class SuccessionAsUsageImpl implements Feature {
    final String hint1, hint2;



    /** Warning, in SysML AST, a SucessionAsUsage isa a both a Feature and a Relationship.
        It references nodes that are available by other means (--> risk of cycle) */
    public SuccessionAsUsageImpl(org.omg.sysml.lang.sysml.SuccessionAsUsage u) {

       var related = u.getRelatedElement();
       if (related.size()!=2)
           throw new InvalidParameterException("Unexpected number of related elements.");
       var el1 = related.get(0);
       var el2 = related.get(1);
       this.hint1 = buildHint(el1);
       this.hint2 = buildHint(el2);
    }

    @Override
    public String toString() {
        return "SUCCESSION " + hint1 + " -> " + hint2 ;
    }

    static String buildHint(org.omg.sysml.lang.sysml.Element e){
        if (e.getDeclaredName() != null) return e.getDeclaredName();
        else return e.getClass().getSimpleName() + ":" + e.path();
    }
}
