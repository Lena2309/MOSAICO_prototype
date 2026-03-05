package org.example.parser.util;

import org.example.agents.*;
import org.omg.sysml.lang.sysml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility interface for mapping SysML {@link PartDefinition} elements to {@link MosaicoAgent} instances.
 * <p>
 * This mapper extracts agent metadata such as IDs, descriptions, and logical constraints
 * by traversing the structural definition of the SysML Part.
 */
public interface PartMapper {

    /**
     * Entry point to transform a PartDefinition into a specific MosaicoAgent and register it in the provided map.
     *
     * @param partDefinition The SysML part definition representing an agent.
     * @param agents         The map where the resulting agent will be stored, keyed by its part name.
     */
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

    /**
     * Identifies the agent type by looking for a Subclassification relationship.
     * * @return The name of the superclass (e.g., "ReferenceAgent") or null if not found.
     */
    private static String findSuperclassName(Element element) {
        if (element == null) return null;

        for (var rel : element.getOwnedRelationship()) {
            if (rel instanceof Subclassification sub) {
                return UtilAttributeMapper.getSafeName(sub.getSuperclassifier());
            }
        }

        // Recursive search in child elements
        for (Element child : element.getOwnedElement()) {
            var name = findSuperclassName(child);
            if (name != null) return name;
        }
        return null;
    }

    /**
     * Extracts values for specific attributes (like 'id' or 'description') by
     * identifying Redefinition features and their associated FeatureValues.
     */
    private static String extractPropertyValue(Element current, String targetProperty) {
        if (current == null) return "";

        boolean targetFound = false;
        String foundValue = "";

        for (var rel : current.getOwnedRelationship()) {
            // Check if this relationship redefines the target property
            if (rel instanceof Redefinition redefinition) {
                var name = UtilAttributeMapper.getSafeName(redefinition.getRedefinedFeature());
                if (targetProperty.equals(name)) {
                    targetFound = true;
                }
            }

            // Extract literal value if present
            if (rel instanceof FeatureValue fv) {
                for (var valNode : fv.getOwnedRelatedElement()) {
                    if (valNode instanceof LiteralString ls) {
                        foundValue = ls.getValue();
                    }
                }
            }
        }

        // Valid match: the element redefines the target AND specifies a value
        if (targetFound && !foundValue.isBlank()) {
            return foundValue;
        }

        // Deep traversal if not found at this level
        for (var child : current.getOwnedElement()) {
            String deepVal = extractPropertyValue(child, targetProperty);
            if (!deepVal.isBlank()) return deepVal;
        }

        return "";
    }

    /**
     * Recursively collects all ConstraintUsage elements within the part to build the agent's behavior rules.
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

    /**
     * Parses a ConstraintUsage into a readable string (e.g., "param > 10").
     */
    private static String findConstraintOperator(Element e) {
        StringBuilder sb = new StringBuilder();

        if (e instanceof OperatorExpression op) {
            sb.append(parseConstraintText(op.getOwnedElement().getFirst()))
                    .append(" ").append(op.getOperator())
                    .append(" ").append(parseConstraintText(op.getOwnedElement().getLast()));
            return sb.toString();
        } else {
            for (var child : e.getOwnedElement()) {
                var result = findConstraintOperator(child);
                if (!result.isBlank()) return result;
            }
        }
        return "";
    }

    /**
     * Resolves the text representation of a constraint operand (Literal, Feature, or nested).
     */
    private static String parseConstraintText(Element e) {
        return switch (e) {
            case LiteralString ls -> ls.getValue();
            case LiteralInteger li -> String.valueOf(li.getValue());
            case FeatureReferenceExpression fre -> UtilAttributeMapper.getSafeName(fre.getReferent());
            default -> {
                for (var child : e.getOwnedElement()) {
                    var result = parseConstraintText(child);
                    if (!result.isBlank()) yield result;
                }
                yield "";
            }
        };
    }

    /**
     * Factory method to instantiate the concrete agent subclass based on the SysML type name.
     */
    static MosaicoAgent createAgent(String typeName, String id, String agentName, String description, List<String> constraints) {
        return switch (typeName) {
            case "ReferenceAgent" -> new ReferenceAgent(id, agentName, description, constraints);
            case "ConsensusAgent" -> new ConsensusAgent(id, agentName, description, constraints);
            case "SupervisionAgent" -> new SupervisionAgent(id, agentName, description, constraints);
            default -> new SolutionAgent(id, agentName, description, constraints);
        };
    }
}