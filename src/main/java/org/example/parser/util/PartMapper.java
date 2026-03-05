package org.example.parser.util;

import org.example.agents.*;
import org.omg.sysml.lang.sysml.*;

import java.util.*;

public interface PartMapper {
    static void mapPartToAgents(PartDefinition partDefinition, Map<String, MosaicoAgent> agents) {
        var typeName = findSuperclassName(partDefinition);
        var agentId = extractPropertyValue(partDefinition, "id");
        var description = extractPropertyValue(partDefinition, "description");
        var partName = partDefinition.getDeclaredName();

        var constraints = new ArrayList<String>();
        collectConstraints(partDefinition, constraints);

        if (typeName == null || typeName.isBlank()) {
            typeName = "SolutionAgent";
        }
        if (agentId == null || agentId.isBlank()) {
            agentId = UUID.randomUUID().toString();
        }
        if (partName == null || partName.isBlank()) {
            partName = "<unnamed>";
        }

        if (description == null) {
            description = "";
        }

        var agent = createAgent(typeName, agentId, "", description, constraints);
        agents.put(partName, agent);
    }

    private static String findSuperclassName(Element element) {
        if (element == null) return null;

        for (var rel : element.getOwnedRelationship()) {
            if (rel instanceof Subclassification sub) {
                return UtilAttributeMapper.getSafeName(sub.getSuperclassifier());
            }
        }

        for (Element child : element.getOwnedElement()) {
            var name = findSuperclassName(child);
            if (name != null) return name;
        }
        return null;
    }

    private static String extractPropertyValue(Element current, String targetProperty) {
        if (current == null) return "";

        boolean targetFound = false;
        String foundValue = "";

        for (var rel : current.getOwnedRelationship()) {
            if (rel instanceof Redefinition redefinition) {
                var name = UtilAttributeMapper.getSafeName(redefinition.getRedefinedFeature());
                if (targetProperty.equals(name)) {
                    targetFound = true;
                }
            }

            if (rel instanceof FeatureValue fv) {
                for (var valNode : fv.getOwnedRelatedElement()) {
                    if (valNode instanceof LiteralString ls) {
                        foundValue = ls.getValue();
                    }
                }
            }
        }

        // If this container is BOTH a redefinition of the target AND contains a value, return it.
        if (targetFound && !foundValue.isBlank()) {
            return foundValue;
        }

        // Otherwise, dig deeper
        for (var child : current.getOwnedElement()) {
            String deepVal = extractPropertyValue(child, targetProperty);
            if (!deepVal.isBlank()) return deepVal;
        }

        return "";
    }
    /**
     * Collects every constraint found anywhere inside the part.
     */
    private static void collectConstraints(Element current, List<String> list) {
        if (current == null) return;

        if (current instanceof ConstraintUsage cu) {
            String text = findConstraintOperator(cu);
            if (!text.isBlank()) {
                list.add(text);
            }
        } else {
            for (Element child : current.getOwnedElement()) {
                collectConstraints(child, list);
            }
        }
    }

    private static String findConstraintOperator(Element e) {
        StringBuilder sb = new StringBuilder();

        if (e instanceof OperatorExpression op) {
            sb.append(parseConstraintText(op.getOwnedElement().getFirst()))
                    .append(" ").append(op.getOperator())
                    .append(" "). append(parseConstraintText(op.getOwnedElement().getLast()));
            return sb.toString();
        } else {
            for (var child : e.getOwnedElement()) {
                var result = findConstraintOperator(child);
                if (!result.isBlank()) return result;
            }
        }
        return "";
    }

    private static String parseConstraintText(Element e) {
        return switch (e) {
            case LiteralString ls -> ls.getValue();
            case LiteralInteger li -> String.valueOf(li.getValue());
            case FeatureReferenceExpression fre -> UtilAttributeMapper.getSafeName(fre.getReferent());
            default -> {
                for (var child : e.getOwnedElement()) {
                    var result = parseConstraintText(child);
                    if (!result.isBlank()) {
                        yield result;
                    }
                }
                yield "";

            }
        };
    }

    static MosaicoAgent createAgent(String typeName, String id, String agentName, String description, List<String> constraints) {
        return switch(typeName) {
            case "ReferenceAgent" -> new ReferenceAgent(id, agentName, description, constraints);
            case "ConsensusAgent" -> new ConsensusAgent(id, agentName, description, constraints);
            case "SupervisionAgent" -> new SupervisionAgent(id, agentName, description, constraints);
            default -> new SolutionAgent(id, agentName, description, constraints);
        };
    }
}