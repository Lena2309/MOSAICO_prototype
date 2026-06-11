package eu.mosaico_project.shadow_sysml.expression;

import eu.mosaico_project.shadow_sysml.impl.ElementImpl;

public class ProxyExpressionImpl extends ElementImpl implements Expression {
    public ProxyExpressionImpl(org.omg.sysml.lang.sysml.Expression e) {
        super(e);
    }
    @Override
    public String toString() {
        return "PROXY";
    }
}
