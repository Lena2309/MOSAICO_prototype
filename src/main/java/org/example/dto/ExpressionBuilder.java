package org.example.dto;


import org.omg.sysml.lang.sysml.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ExpressionBuilder {
    public static org.example.dto.Expression transpile(org.omg.sysml.lang.sysml.Expression e){
        switch (e) {
            case FeatureReferenceExpression r :
                throw new InvalidParameterException("FeatureReferenceExpression not tested.");

            case FeatureChainExpression c :
                List<Relationship> identifiers =  c.getTargetFeature().getOwnedRelationship();
                List<String> id2 = identifiers.stream().map((f)->f.getTarget().getFirst().getDeclaredName()).toList();
                String outername =
                        c.getTargetFeature().getOwnedRelationship().get(1)
                                .getTarget().getFirst().getOwningNamespace().getOwningNamespace().getDeclaredName() ; // fixme
                List<String> id3 = new LinkedList<>(id2);
                id3.addFirst(outername);
                return new DotExpression(id3);

            case LiteralString s :
                throw new InvalidParameterException("String Literals cannot be used as loop conditions. (" + s.getValue() + ")");
            default :
                throw new InvalidParameterException("Expression not supported: " + e.getClass());
        }

    }
}
