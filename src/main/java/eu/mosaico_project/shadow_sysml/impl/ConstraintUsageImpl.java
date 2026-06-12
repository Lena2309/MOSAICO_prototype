package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.Usage;
import eu.mosaico_project.shadow_sysml.expression.Expression;
import org.omg.sysml.lang.sysml.ResultExpressionMembership;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ConstraintUsageImpl extends ElementImpl implements Expression, Usage {
    List<Expression> results = new ArrayList<>();
    public ConstraintUsageImpl(org.omg.sysml.lang.sysml.ConstraintUsage c) {
        super(c);
        for (var r : c.getOwnedRelationship()) {
            switch (r){
                case ResultExpressionMembership m -> results.add(Simplifier.simplifyExpression(m.getOwnedResultExpression()));
                default ->
                        throw new InvalidParameterException("[CONSTRAINT] fixme " + r.getClass().getSimpleName());
            }

        }

    }
}
