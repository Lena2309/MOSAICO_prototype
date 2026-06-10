package eu.mosaico_project.shadow_sysml.expression;

import eu.mosaico_project.shadow_sysml.Simplifier;

import java.util.List;

public class OperatorExpressionImpl implements Expression {
    final String operator;
    final List<Expression> parameters ;
    public OperatorExpressionImpl(org.omg.sysml.lang.sysml.OperatorExpression op){
        this.operator = op.getOperator();
        this.parameters = Simplifier.simplifyExpressionList(op.getArgument());
    }

    @Override
    public String toString(){
        return "OP:" + this.operator;
    }

    @Override
    public String getDeclaredName() {
        return "NO NAME";
    }
}
