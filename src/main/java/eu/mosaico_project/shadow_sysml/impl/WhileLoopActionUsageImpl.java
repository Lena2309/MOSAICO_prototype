package eu.mosaico_project.shadow_sysml.impl;

import eu.mosaico_project.shadow_sysml.Feature;
import eu.mosaico_project.shadow_sysml.Simplifier;
import eu.mosaico_project.shadow_sysml.expression.Expression;

public class WhileLoopActionUsageImpl extends ActionUsageImpl implements Feature {
    final Expression condition;

    /** true for While loops, false for Until loops. */
    final boolean whileLoop;

    public WhileLoopActionUsageImpl(org.omg.sysml.lang.sysml.WhileLoopActionUsage u) {
        super(u);

        org.omg.sysml.lang.sysml.Expression wc = u.getWhileArgument();
        org.omg.sysml.lang.sysml.Expression uc = u.getUntilArgument();

        assert (((wc != null) || (uc != null)) && ( (wc == null) || (uc == null) ) ); // XOR

        if (wc!=null) {
            this.condition = Simplifier.simplifyExpression(wc);
            this.whileLoop = true ;
        }
        else {
            this.condition = Simplifier.simplifyExpression(uc);
            this.whileLoop = false;
        }
    }

    @Override
    public String toString(){
        return (this.whileLoop ? "WHILE LOOP" : "UNTIL LOOP");
    }
}
