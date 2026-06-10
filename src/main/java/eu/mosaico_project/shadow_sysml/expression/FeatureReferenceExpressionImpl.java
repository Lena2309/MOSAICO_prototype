package eu.mosaico_project.shadow_sysml.expression;

import org.omg.sysml.lang.sysml.FeatureReferenceExpression;

public class FeatureReferenceExpressionImpl implements Expression {
    public FeatureReferenceExpressionImpl(FeatureReferenceExpression e) {
    }

    @Override
    public String getDeclaredName() {
        return "NO NAME";
    }
}
