package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Element;
import eu.mosaico_project.shadow_sysml.Feature;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.expression.Expression;
import org.omg.sysml.lang.sysml.MultiplicityRange;

import java.security.InvalidParameterException;

public class MultiplicityRangeImpl extends ElementImpl implements Feature {
    final Expression lowerBound, upperBound;
    //static int cpt=0;
    public MultiplicityRangeImpl(MultiplicityRange m) {
        super(m);
        if (m.eIsProxy())
            throw new InvalidParameterException("[MultiplicityRange] Proxy");
        //cpt++;
        //System.out.println(cpt + m.path());

//        org.omg.sysml.lang.sysml.Expression low = m.getLowerBound();
//        org.omg.sysml.lang.sysml.Expression up = m.getUpperBound(); // FIXME  Stack overflow
        org.omg.sysml.lang.sysml.Expression low = null;
        org.omg.sysml.lang.sysml.Expression up = null;

        if (low!=null)
            this.lowerBound = Simplifier.simplifyExpression(low);
        else
            this.lowerBound = null;


        if (up!=null)
            this.upperBound = Simplifier.simplifyExpression(up);
        else
            this.upperBound = null;
    }

    @Override
    public String toString() {
        return "Multiplicity [" + lowerBound + " " + upperBound +"]";
    }
}
