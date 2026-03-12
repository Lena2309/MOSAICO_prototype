package org.example.parser.util;

import org.example.agents.MosaicoAgent;
import org.example.agents.SolutionAgent;
import org.example.dto.Task;
import org.omg.sysml.lang.sysml.*;

import java.util.*;

/**
 * Utility interface responsible for mapping SysML {@link ActionUsage} elements
 * to execution-ready {@link Task} objects.
 */
public interface ActionMapper {

    /**
     * Maps a SysML ActionUsage to a domain Task by resolving its properties and associated agent.
     *
     * @param action             The SysML action usage to be parsed.
     * @param mosaicoAgents      List of currently available agents to match against.
     * @param outputDependencies Tasks that must be completed before this task can execute.
     * @param executionOrder     The sequence index of this task in the workflow.
     * @return A populated {@link Task} instance.
     */
    static Task mapActionToTask(ActionUsage action, List<MosaicoAgent> mosaicoAgents, List<Task> outputDependencies, int executionOrder) {
        var propertyMap = new HashMap<String, String>();
        propertyMap = (HashMap<String, String>) findActionProperties(action, propertyMap);

        var finalAgentName = propertyMap.get("agentName");

        // Attempt to find an existing agent that matches the requirement
        var agentForTask = mosaicoAgents.stream()
                .filter(agent -> Objects.equals(finalAgentName, agent.getName()))
                .findFirst();

        // Fallback: Create a generic SolutionAgent if no specific match is found
        if (agentForTask.isEmpty()) {
            agentForTask = Optional.of(new SolutionAgent(UUID.randomUUID().toString(), propertyMap.get("agentName"), null, null));
        }

        var outputs = new ArrayList<String>();
        action.getOutput().stream()
                .map(Element::getDeclaredName)
                .forEach(outputs::add);

        return new Task(
                executionOrder,
                action.getDeclaredName(),
                propertyMap.get("description"),
                outputs,
                agentForTask.get(),
                outputDependencies
        );
    }

    /**
     * Recursively traverses the SysML element tree to extract specific metadata
     * (e.g., description and agent name) from redefined features or feature values.
     *
     * @param e           The current SysML element being inspected.
     * @param propertyMap The accumulator map for extracted properties.
     * @return The updated property map.
     */
    private static Map<String, String> findActionProperties(Element e, Map<String, String> propertyMap) {
        // Optimization: stop recursion if all required fields are found
        if (propertyMap.containsKey("description") && propertyMap.containsKey("agentName")) {
            return propertyMap;
        }

        var redefinedDescription = false;
        var redefinedAgentName = false;

        for (var i = 0; i < e.getOwnedRelationship().size(); i++) {
            if (e.getOwnedRelationship().get(i) instanceof Redefinition rd) {
                var targetName = UtilAttributeMapper.getSafeName(rd.getRedefinedFeature());
                if ("description".equals(targetName)) {
                    redefinedDescription = true;
                }
                if ("agent".equals(targetName)) {
                    redefinedAgentName = true;
                }
            }

            // Extract values if a redefinition is detected
            if (redefinedDescription || redefinedAgentName) {
                var child = i < e.getOwnedRelationship().size() ? e.getOwnedRelationship().get(i + 1) : e.getOwnedRelationship().get(i - 1);
                if (child instanceof FeatureValue fv) {
                    var propertyValue = e.getOwnedElement().getFirst();

                    if (propertyValue instanceof LiteralString ls) {
                        propertyMap.put("description", ls.getValue());
                        return propertyMap;
                    }
                    if (propertyValue instanceof FeatureReferenceExpression fre) {
                        propertyMap.put("agentName", UtilAttributeMapper.getSafeName(fre.getReferent()));
                        return propertyMap;
                    }
                }
            }
            // Recursive call on relationships
            findActionProperties(e.getOwnedRelationship().get(i), propertyMap);
        }

        // Recursive call on child elements
        for (var child : e.getOwnedElement()) {
            findActionProperties(child, propertyMap);
        }
        return propertyMap;
    }
}