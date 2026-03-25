package org.example.dto.expression;


import org.omg.sysml.lang.sysml.*;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

public class ExpressionBuilder {

    public static Expression transpile(org.omg.sysml.lang.sysml.Expression e) {
        switch (e) {

            case FeatureChainExpression c:
                List<Relationship> references = c.getTargetFeature().getOwnedRelationship();
                List<String> names =
                        references.stream().map((f) -> f.getTarget().getFirst().getDeclaredName())
                                .toList();
                String outername =
                        c.getTargetFeature().getOwnedRelationship().get(1)
                                .getTarget().getFirst().getOwningNamespace().getOwningNamespace()
                                .getDeclaredName(); // fixme
                List<String> all_names = new LinkedList<>(names);
                all_names.addFirst(outername);
                return new DotExpression(all_names);

            case FeatureReferenceExpression r:
                throw new InvalidParameterException("FeatureReferenceExpression not tested.");

            case LiteralBoolean b:
                return new LiteralBooleanExpression(b.isValue());

            case LiteralString s:
                throw new InvalidParameterException("String Literals cannot be used as loop conditions. (" + s.getValue() + ")");

            case OperatorExpression op: {
                var operator = op.getOperator();
                switch (operator) {

                    case "&": {
                        var params = op.getOwnedRelationship();
                        var arg1 = (org.omg.sysml.lang.sysml.Expression) params.get(0).getOwnedRelatedElement().getFirst().getOwnedRelationship().getFirst().getOwnedRelatedElement().getFirst();
                        var arg2 = (org.omg.sysml.lang.sysml.Expression) params.get(1).getOwnedRelatedElement().getFirst().getOwnedRelationship().getFirst().getOwnedRelatedElement().getFirst();
                        return new ConjunctionExpression(transpile(arg1), transpile(arg2));
                    }

                    default:
                        throw new InvalidParameterException("Operator not implemented: " + operator);
                }
            }

            default:
                throw new InvalidParameterException("Expression not supported: " + e.getClass());
        }

    }
}
