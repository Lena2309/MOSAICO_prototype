package eu.mosaico_project.shadow_sysml;

import eu.mosaico_project.shadow_sysml.expression.*;
import eu.mosaico_project.shadow_sysml.expression.FeatureChainExpressionImpl;
import eu.mosaico_project.shadow_sysml.impl.*;
import org.eclipse.emf.ecore.EObject;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;

public class Simplifier {

    public static Element simplify(org.omg.sysml.lang.sysml.Namespace node){
        return new NamespaceImpl(node);
    }

    public static Element simplifyElement(org.omg.sysml.lang.sysml.Element e){
        return switch (e) {
            // routing
            case org.omg.sysml.lang.sysml.Expression ex -> Simplifier.simplifyExpression(ex);
            case org.omg.sysml.lang.sysml.Feature f -> simplifyFeature(f);
            case org.omg.sysml.lang.sysml.Type t -> simplifyType(t);
            case org.omg.sysml.lang.sysml.Relationship r ->
                    throw new InvalidParameterException("[ELEMENT] Relationships should be classified, not simplifed. " + r.getClass().getSimpleName());

            case org.omg.sysml.lang.sysml.Package p -> new PackageImpl(p);
            case org.omg.sysml.lang.sysml.Documentation d -> new DocumentationImpl();
            case org.omg.sysml.lang.sysml.Comment c -> new CommentImpl();

            default->
                throw new InvalidParameterException("[ELEMENT] Not supported: " + e.getClass().getSimpleName());
        } ;
    }


    public static Feature simplifyFeature(org.omg.sysml.lang.sysml.Feature f){
        if (f.eIsProxy())
            return new ProxyFeatureImpl(f);
        return switch (f) {

            // route Expression <: Feature
            case org.omg.sysml.lang.sysml.Expression e -> simplifyExpression(e);

            case org.omg.sysml.lang.sysml.ReferenceUsage r -> new ReferenceUsageImpl(r);
            case org.omg.sysml.lang.sysml.AttributeUsage u -> new AttributeUsageImpl(u);
            case org.omg.sysml.lang.sysml.AssignmentActionUsage u -> new AssignmentActionUsageImpl(u);
            case org.omg.sysml.lang.sysml.WhileLoopActionUsage u -> new WhileLoopActionUsageImpl(u);
            case org.omg.sysml.lang.sysml.TransitionUsage t -> new TransitionUsageImpl(t);
            case org.omg.sysml.lang.sysml.IfActionUsage u -> new IfActionUsageImpl(u); // fixme: never reached.
            case org.omg.sysml.lang.sysml.ActionUsage u -> new ActionUsageImpl(u);
            case org.omg.sysml.lang.sysml.MultiplicityRange m -> new MultiplicityRangeImpl(m);
            case org.omg.sysml.lang.sysml.impl.FeatureImpl i -> new FeatureImpl(i);
            default ->
                    throw new InvalidParameterException("[FEATURE] Not supported: " + f.getClass().getSimpleName());
        };
    }

    public static Expression simplifyExpression(org.omg.sysml.lang.sysml.Expression e){
        if (e.eIsProxy())
            return new ProxyExpressionImpl(e);
        return switch (e) {
            case org.omg.sysml.lang.sysml.Invariant b -> new Invariant(b);
            case org.omg.sysml.lang.sysml.LiteralInteger i -> new LiteralIntegerImpl(i);
            case org.omg.sysml.lang.sysml.LiteralInfinity i -> new LiteralInfinityImpl(i);
            case org.omg.sysml.lang.sysml.LiteralString i -> new LiteralStringImpl(i);
            case org.omg.sysml.lang.sysml.SelectExpression op -> new SelectExpressionImpl(op);
            case org.omg.sysml.lang.sysml.FeatureChainExpression op -> new FeatureChainExpressionImpl(op);
            case org.omg.sysml.lang.sysml.OperatorExpression op -> new OperatorExpressionImpl(op);
            case org.omg.sysml.lang.sysml.FeatureReferenceExpression op ->
                    new FeatureReferenceExpressionImpl(op);
            case org.omg.sysml.lang.sysml.ConstraintUsage u -> new ConstraintUsageImpl(u);
            case org.omg.sysml.lang.sysml.InvocationExpression i -> new InvocationExpressionImpl(i);
            case org.omg.sysml.lang.sysml.NullExpression n -> new NullExpressionImpl(n);
            case org.omg.sysml.lang.sysml.impl.ExpressionImpl ei -> new ExpressionImpl(ei);
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

    public static List<Type> simplifyTypeList(List<? extends org.omg.sysml.lang.sysml.Type> s){
        return s.stream().map(Simplifier::simplifyType).toList();
    }


    public static List<Feature> simplifyFeatureList(List<? extends org.omg.sysml.lang.sysml.Feature> s){
        return s.stream().map(Simplifier::simplifyFeature).toList();
    }


    public static Type simplifyType(org.omg.sysml.lang.sysml.Type t) {
        if (t.eIsProxy())
            return new ProxyTypeImpl(t);
        return switch (t) {
            //routing
            case org.omg.sysml.lang.sysml.Feature f -> simplifyFeature(f);

            case org.omg.sysml.lang.sysml.PartDefinition p -> new PartDefinitionImpl(p);
            case org.omg.sysml.lang.sysml.ActionDefinition d -> new ActionDefinitionImpl(d);
            case org.omg.sysml.lang.sysml.DataType d -> new DataTypeImpl(d);
            case org.omg.sysml.lang.sysml.Function f -> new FunctionImpl(f);
            case org.omg.sysml.lang.sysml.Behavior b -> new BehaviorImpl(b);
            case org.omg.sysml.lang.sysml.Classifier c -> new ClassifierImpl(c);
            default ->
                    throw new InvalidParameterException("[EXPRESSION] Not supported: " + t.getClass().getSimpleName());
        } ;
    }

    public static void discard(org.omg.sysml.lang.sysml.Element e){

    }

}
