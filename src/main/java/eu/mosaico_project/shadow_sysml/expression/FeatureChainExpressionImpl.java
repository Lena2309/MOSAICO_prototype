package eu.mosaico_project.shadow_sysml.expression;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureChainExpression;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;

public class FeatureChainExpressionImpl implements Expression {

    final Element target;

    public FeatureChainExpressionImpl(FeatureChainExpression e) {
        var ref = e.getTargetFeature();
        this.target = Simplifier.simplifyFeature(ref);
    }


    @Override
    public String toString() {
        return "REF to=" + target;
    }
}
