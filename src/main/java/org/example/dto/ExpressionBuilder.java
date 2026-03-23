package org.example.dto;


import org.omg.sysml.lang.sysml.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ExpressionBuilder {

    public static org.example.dto.Expression transpile(org.omg.sysml.lang.sysml.Expression e){
        switch (e) {

            case FeatureChainExpression c :
                List<Relationship> references =  c.getTargetFeature().getOwnedRelationship();
                List<String> names =
                        references.stream().map((f)->f.getTarget().getFirst().getDeclaredName())
                                .toList();
                String outername =
                        c.getTargetFeature().getOwnedRelationship().get(1)
                                .getTarget().getFirst().getOwningNamespace().getOwningNamespace()
                                .getDeclaredName() ; // fixme
                List<String> all_names = new LinkedList<>(names);
                all_names.addFirst(outername);
                return new DotExpression(all_names);

            case FeatureReferenceExpression r :
                throw new InvalidParameterException("FeatureReferenceExpression not tested.");

            case LiteralString s :
                throw new InvalidParameterException("String Literals cannot be used as loop conditions. (" + s.getValue() + ")");
            default :
                throw new InvalidParameterException("Expression not supported: " + e.getClass());
        }

    }
}
