package eu.mosaico_project.shadow_sysml;

import eu.mosaico_project.shadow_sysml.expression.*;
import eu.mosaico_project.shadow_sysml.expression.FeatureChainExpressionImpl;
import eu.mosaico_project.shadow_sysml.impl.*;
import org.eclipse.emf.ecore.EObject;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.Relationship;


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
            case org.omg.sysml.lang.sysml.Expression ex -> Simplifier.simplifyExpression(ex);
            case org.omg.sysml.lang.sysml.Feature f -> simplifyFeature(f);
            case  org.omg.sysml.lang.sysml.Relationship r -> simplifyRelationship(r);

            case org.omg.sysml.lang.sysml.Package p -> new PackageImpl(p);
            case org.omg.sysml.lang.sysml.PartDefinition p -> new PartDefinitionImpl(p);
            case org.omg.sysml.lang.sysml.ActionDefinition d -> new ActionDefinitionImpl(d);
            case org.omg.sysml.lang.sysml.Documentation d -> new DocumentationImpl(d);
            case org.omg.sysml.lang.sysml.DataType d -> new DataTypeImpl(d);
            case org.omg.sysml.lang.sysml.Function f -> new FunctionImpl(f);
            case org.omg.sysml.lang.sysml.Behavior b -> new BehaviorImpl(b);
            case org.omg.sysml.lang.sysml.Classifier c -> new ClassifierImpl(c);

            default->
                throw new InvalidParameterException("[ELEMENT] Not supported: " + e.getClass().getSimpleName());
        } ;
    }

    /** Relationships explain how a member is related to its container. */
    static Element simplifyRelationship(Relationship r) {
        return switch (r) {
            case org.omg.sysml.lang.sysml.Subclassification st -> new SubclassificationImpl(st);
            case org.omg.sysml.lang.sysml.FeatureValue v -> new FeatureValueImpl(v);
            case org.omg.sysml.lang.sysml.OwningMembership m -> new OwningMembershipImpl(m); // warning on subtyping here
            case org.omg.sysml.lang.sysml.Membership m -> new MembershipImpl(m);
            case org.omg.sysml.lang.sysml.SuccessionAsUsage u -> new SuccessionAsUsageImpl(u);
            case org.omg.sysml.lang.sysml.FeatureTyping ft -> new FeatureTypingImpl(ft);
            case org.omg.sysml.lang.sysml.Redefinition rs -> new RedefinitionImpl(rs);
            case org.omg.sysml.lang.sysml.Association a -> new AssociationImpl(a);

            default ->
                    throw new InvalidParameterException("[RELATIONSHIP] Not supported: " + r.getClass().getSimpleName());
        };
    }

    public static Element simplifyFeature(Feature f){
        if (f.eIsProxy())
            return new ProxyElementImpl(f);
        return switch (f) {
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
            case org.omg.sysml.lang.sysml.FeatureReferenceExpression op -> new FeatureReferenceExpressionImpl(op);
            case org.omg.sysml.lang.sysml.ConstraintUsage u -> new ConstraintUsageImpl(u);
            case org.omg.sysml.lang.sysml.InvocationExpression i -> new InvocationExpressionImpl(i);
            case org.omg.sysml.lang.sysml.NullExpression n -> new NullExpressionImpl(n);
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
    public static List<Element> simplifyRelationshipList(List<? extends org.omg.sysml.lang.sysml.Relationship> s){
        return s.stream().map(Simplifier::simplifyRelationship).toList();
    }
}

