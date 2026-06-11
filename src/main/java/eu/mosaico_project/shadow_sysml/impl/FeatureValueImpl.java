package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.expression.Expression;
import org.omg.sysml.lang.sysml.FeatureValue;

public class FeatureValueImpl extends ElementImpl implements Element {

    final Expression expr;

    public FeatureValueImpl(FeatureValue v) {
        super(v);
        this.expr = Simplifier.simplifyExpression(v.getValue());
    }

    @Override
    public String toString() {
        return "F_VALUE " + expr ;
    }
}
