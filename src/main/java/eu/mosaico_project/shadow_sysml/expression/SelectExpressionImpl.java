package eu.mosaico_project.shadow_sysml.expression;

import org.omg.sysml.lang.sysml.SelectExpression;

import java.security.InvalidParameterException;

public class SelectExpressionImpl extends OperatorExpressionImpl implements Expression {
    final org.eclipse.emf.common.util.URI refersTo;

    public SelectExpressionImpl(SelectExpression op) {
        super(op);
        if (this.parameters.size()<2) {
            if (op.eIsProxy())
                throw new InvalidParameterException("[PROXY] Parameters not found, forward reference?");
            else
               throw new InvalidParameterException("[NOT PROXY] Parameters not found, forward reference?");
        }
        refersTo = ((org.omg.sysml.lang.sysml.impl.SelectExpressionImpl)op).eProxyURI();
    }
    @Override
    public String toString() {
        return "SELECT";
    }
}
