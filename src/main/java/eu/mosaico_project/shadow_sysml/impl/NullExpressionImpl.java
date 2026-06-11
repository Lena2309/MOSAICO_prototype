package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.expression.Expression;
import org.omg.sysml.lang.sysml.NullExpression;

/** NullExpressions are used for empty data. */
public class NullExpressionImpl extends ElementImpl implements Expression {

    public NullExpressionImpl(NullExpression n) {
        super(n);
    }

    @Override
    public String toString() {
        return "EMPTY";
    }
}
