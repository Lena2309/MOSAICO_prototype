package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.ReferenceUsage;
import eu.mosaico_project.shadow_sysml.Simplifier;
import org.jspecify.annotations.Nullable;
import org.omg.sysml.lang.sysml.FeatureDirectionKind;

import java.util.List;

public class ReferenceUsageImpl extends ElementImpl implements ReferenceUsage {

    final List<Element> classifiers;
    final List<Element> relations;

    @Nullable
    FeatureDirectionKind direction ;


    public ReferenceUsageImpl(org.omg.sysml.lang.sysml.ReferenceUsage r) {
        super(r);

        this.classifiers = Simplifier.simplifyElementList(r.getDefinition());
        this.relations = Simplifier.simplifyRelationshipList(r.getOwnedRelationship());

        if (r.getDirection() != null)  this.direction = r.getDirection();
    }



    @Override
    public String toString() {
        return "REF_USAGE " + ( declaredName!=null ? declaredName : "_")  + " " + (this.direction!= null ? "(" + this.direction + ")": "");
    }
}
