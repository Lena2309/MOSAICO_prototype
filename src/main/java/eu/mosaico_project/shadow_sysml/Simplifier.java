package eu.mosaico_project.shadow_sysml;

import eu.mosaico_project.shadow_sysml.expression.Expression;
import eu.mosaico_project.shadow_sysml.expression.FeatureReferenceExpressionImpl;
import eu.mosaico_project.shadow_sysml.expression.Invariant;
import eu.mosaico_project.shadow_sysml.expression.OperatorExpressionImpl;
import eu.mosaico_project.shadow_sysml.impl.*;
import org.eclipse.emf.ecore.EObject;


import java.security.InvalidParameterException;
import java.util.List;

public class Simplifier {
    public static Element simplify(EObject node){
        switch (node){
            case org.omg.sysml.lang.sysml.Namespace n : return new NamespaceImpl(n) ;
            default :
                throw new InvalidParameterException("[ROOT] Not supported: " + node.getClass().getSimpleName());
        }
    }

    public static Element simplifyElement(org.omg.sysml.lang.sysml.Element e){
        return switch (e) {
            case org.omg.sysml.lang.sysml.Package p -> new PackageImpl(p);
            case org.omg.sysml.lang.sysml.PartDefinition p -> new PartDefinitionImpl(p);
            case org.omg.sysml.lang.sysml.ReferenceUsage r -> new ReferenceUsageImpl(r);
            case org.omg.sysml.lang.sysml.ActionDefinition d -> new ActionDefinitionImpl(d);
            case org.omg.sysml.lang.sysml.Documentation d -> new DocumentationImpl(d);
            case org.omg.sysml.lang.sysml.DataType d -> new DataTypeImpl(d);
            case org.omg.sysml.lang.sysml.Function f -> new FunctionImpl(f);
            case org.omg.sysml.lang.sysml.Behavior b -> new BehaviorImpl(b);
            case org.omg.sysml.lang.sysml.AttributeUsage u -> new AttributeUsageImpl(u);
            case org.omg.sysml.lang.sysml.AssignmentActionUsage u -> new AssignmentActionUsageImpl(u);
            case org.omg.sysml.lang.sysml.WhileLoopActionUsage u -> new WhileLoopActionUsageImpl(u);
            case org.omg.sysml.lang.sysml.TransitionUsage t -> new TransitionUsageImpl(t);
            case org.omg.sysml.lang.sysml.ActionUsage u -> new ActionUsageImpl(u);
            case org.omg.sysml.lang.sysml.SuccessionAsUsage u -> new SuccessionAsUsageImpl(u);
            case org.omg.sysml.lang.sysml.FeatureTyping ft -> new FeatureTypingImpl(ft);
            case org.omg.sysml.lang.sysml.Redefinition r -> new RedefinitionImpl(r);
            case org.omg.sysml.lang.sysml.ConstraintUsage u -> new ConstraintUsageImpl(u);
            case org.omg.sysml.lang.sysml.FeatureChainExpression c -> new FeatureChainExpressionImpl(c); // FIXME EXPRESSION
            case org.omg.sysml.lang.sysml.OperatorExpression c -> new OperatorExpressionImpl(c); // FIXME EXPRESSION
            default->
                throw new InvalidParameterException("[ELEMENT] Not supported: " + e.getClass().getSimpleName());
        } ;
    }

    public static Expression simplifyExpression(org.omg.sysml.lang.sysml.Expression e){

        return switch (e) {
            case org.omg.sysml.lang.sysml.Invariant b -> new Invariant(b);
            case org.omg.sysml.lang.sysml.LiteralInteger i -> new LiteralIntegerImpl(i);
            case org.omg.sysml.lang.sysml.OperatorExpression op -> new OperatorExpressionImpl(op);
            case org.omg.sysml.lang.sysml.FeatureReferenceExpression op -> new FeatureReferenceExpressionImpl(op);
            default ->
                throw new InvalidParameterException("[EXPRESSION] Not supported: " + e.getClass().getSimpleName());
        } ;

    }

    public static List<Element> simplifyElementList(List<? extends org.omg.sysml.lang.sysml.Element> s){
        return s.stream().map(Simplifier::simplifyElement).toList();
    }
    public static List<Expression> simplifyExpressionList(List<? extends org.omg.sysml.lang.sysml.Expression> s){
        return s.stream().map(Simplifier::simplifyExpression).toList();
    }
}

