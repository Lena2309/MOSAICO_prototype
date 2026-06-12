package eu.mosaico_project.shadow_sysml.expression;


import eu.mosaico_project.shadow_sysml.Feature;
import eu.mosaico_project.shadow_sysml.Simplifier;

import org.omg.sysml.lang.sysml.FeatureReferenceExpression;

public class FeatureReferenceExpressionImpl implements Expression {

    final Feature target;

    public FeatureReferenceExpressionImpl(FeatureReferenceExpression e) {
        org.omg.sysml.lang.sysml.Feature ref = e.getReferent();
        this.target = Simplifier.simplifyFeature(ref);
    }

    @Override
    public String toString() {
        return "REF " + target;
    }
}
