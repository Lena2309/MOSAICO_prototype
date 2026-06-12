package eu.mosaico_project.shadow_sysml.expression;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;

public class FeatureReferenceExpressionImpl implements Expression {

    final Element target;

    public FeatureReferenceExpressionImpl(FeatureReferenceExpression e) {
        Feature ref = e.getReferent();
        this.target = Simplifier.simplifyFeature(ref);
    }

    @Override
    public String toString() {
        return "REF to=" + target;
    }
}
