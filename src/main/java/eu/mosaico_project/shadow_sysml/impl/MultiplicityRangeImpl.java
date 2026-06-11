package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.expression.Expression;
import org.omg.sysml.lang.sysml.MultiplicityRange;

public class MultiplicityRangeImpl extends ElementImpl implements Element {
    final Expression lowerBound, upperBound;
    public MultiplicityRangeImpl(MultiplicityRange m) {
        super(m);
        this.lowerBound = Simplifier.simplifyExpression(m.getLowerBound());
        this.upperBound = Simplifier.simplifyExpression((m.getUpperBound()));
    }
}
