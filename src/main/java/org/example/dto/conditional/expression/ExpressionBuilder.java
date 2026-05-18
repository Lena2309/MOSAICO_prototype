package org.example.dto.conditional.expression;

import org.eclipse.emf.ecore.EObject;
import org.omg.sysml.lang.sysml.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ExpressionBuilder {

    public static Expression transpile(org.omg.sysml.lang.sysml.Expression e) {
        if (e == null) {
            throw new InvalidParameterException("Expression cannot be null.");
        }

        if (e instanceof FeatureChainExpression c) {
            // 1. Use Xtext to grab the raw string
            try {
                var node = org.eclipse.xtext.nodemodel.util.NodeModelUtils.getNode(c);
                if (node != null && node.getText() != null) {
                    // Remove whitespace and split by literal dot
                    String cleanText = node.getText().replaceAll("\\s+", "");
                    if (!cleanText.isEmpty()) {
                        return new DotExpression(List.of(cleanText.split("\\.")));
                    }
                }
            } catch (Exception ignored) {
            }

            // 2. AST fallback
            List<String> chainNames = extractChainNames(c);
            if (chainNames.isEmpty()) {
                throw new InvalidParameterException("Could not extract names from FeatureChainExpression.");
            }
            return new DotExpression(chainNames);
        }

        if (e instanceof FeatureReferenceExpression r) {
            if (r.getReferent() != null && r.getReferent().getDeclaredName() != null) {
                return new IdentifierExpression(r.getReferent().getDeclaredName());
            }
            throw new InvalidParameterException("FeatureReferenceExpression missing referent.");
        }

        if (e instanceof LiteralBoolean b) {
            return new LiteralBooleanExpression(b.isValue());
        }

        if (e instanceof LiteralInteger b) {
            return new LiteralIntegerExpression(b.getValue());
        }


        if (e instanceof LiteralString s) {
            //throw new InvalidParameterException("String Literals cannot be used as loop conditions. (" + s.getValue() + ")");
        }

        if (e instanceof OperatorExpression op) {
            var operator = op.getOperator();
            List<org.omg.sysml.lang.sysml.Expression> operands = findOperands(op);
            final int nbOperands = operands.size();

            // TODO : add operators ( |, ||, ==, !=, not)
            switch (operator) {

                case "&": {
                    if (nbOperands != 2)
                        throw new InvalidParameterException("Operator\" + operator + \"  requires 2 operands, found: " + nbOperands);
                    else
                        return new ConjunctionExpression(transpile(operands.get(0)), transpile(operands.get(1)));
                }

                case "|": {
                    if (nbOperands != 2)
                        throw new InvalidParameterException("Operator" + operator + "  requires 2 operands, found: " + nbOperands);
                    else
                        return new DisjunctionExpression(transpile(operands.get(0)), transpile(operands.get(1)));
                }

                case ">": {
                    if (nbOperands != 2)
                        throw new InvalidParameterException("Operator" + operator + "  requires 2 operands, found: " + nbOperands);
                    else
                        return new GreaterThanExpression(transpile(operands.get(0)), transpile(operands.get(1)));
                }

                case "+": {
                    if (nbOperands != 2)
                        throw new InvalidParameterException("Operator" + operator + "  requires 2 operands, found: " + nbOperands);
                    else
                        return new AddExpression(transpile(operands.get(0)), transpile(operands.get(1)));
                }


                default:
                    throw new InvalidParameterException("Operator not implemented: " + operator);
            }
        }
        throw new InvalidParameterException("Expression not supported: " + e.getClass().getSimpleName());
    }

    /**
     * Fallback method that drills into SysML's FeatureChaining relationships
     * when Xtext text is unavailable.
     */
    private static List<String> extractChainNames(EObject e) {
        List<String> names = new ArrayList<>();
        if (e == null) return names;

        if (e instanceof FeatureChainExpression fce) {
            // 1. Process the base expression (left side)
            for (var child : fce.eContents()) {
                names.addAll(extractChainNames(child));
            }
            // 2. Process the invisible chained proxy feature
            if (fce.getTargetFeature() != null) {
                for (var rel : fce.getTargetFeature().getOwnedRelationship()) {
                    if (rel.getClass().getSimpleName().contains("FeatureChaining")) {
                        for (var target : rel.getTarget()) {
                            String relName = target.getDeclaredName();
                            if (relName != null && !relName.isBlank() && !names.contains(relName)) {
                                names.add(relName);
                            }
                        }
                    }
                }
            }
        } else if (e instanceof FeatureReferenceExpression fre) {
            if (fre.getReferent() != null) {
                String name = fre.getReferent().getDeclaredName();
                if (name != null && !name.isBlank() && !names.contains(name)) {
                    names.add(name);
                }
            }
        } else {
            for (var child : e.eContents()) {
                names.addAll(extractChainNames(child));
            }
        }
        return names;
    }

    private static List<org.omg.sysml.lang.sysml.Expression> findOperands(EObject node) {
        List<org.omg.sysml.lang.sysml.Expression> operands = new ArrayList<>();
        if (node == null) return operands;

        for (var child : node.eContents()) {
            if (child instanceof org.omg.sysml.lang.sysml.Expression expr) {
                operands.add(expr);
            } else if (child instanceof Element) {
                operands.addAll(findOperands(child));
            }
        }
        return operands;
    }
}