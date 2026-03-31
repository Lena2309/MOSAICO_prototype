package org.example.transformer.mapper;

import org.example.agents.mosaico.MosaicoAgent;
import org.example.agents.mosaico.SolutionAgent;
import org.example.dto.step.Step;
import org.example.dto.task.AgentTask;
import org.example.dto.task.output.Channel;
import org.omg.sysml.lang.sysml.*;

import java.util.*;

/**
 * Utility interface responsible for mapping SysML {@link ActionUsage} elements
 * to execution-ready {@link Step} objects.
 */
public interface ActionMapper {

    /**
     * Maps a SysML ActionUsage to a domain Task by resolving its properties and associated agent.
     *
     * @param action             The SysML action usage to be parsed.
     * @param mosaicoAgents      List of currently available agents to match against.
     * @param outputDependencies Tasks that must be completed before this task can execute.
     * @return A populated {@link Step} instance.
     */
    static Step mapActionToAgentTask(ActionUsage action, List<MosaicoAgent> mosaicoAgents, List<AgentTask> outputDependencies, Optional<Step> previousStep) {
        Map<String, String> propertyMap = new HashMap<>();
        populateActionProperties(action, propertyMap);

        var finalAgentName = propertyMap.get("agentName");
        if (finalAgentName == null)
            System.out.println("[WARNING] No agent name specification found.");

        // Attempt to find an existing agent that matches the requirement
        var agentForTask = mosaicoAgents.stream()
                .filter(agent -> Objects.equals(finalAgentName, agent.getName()))
                .findFirst();

        // Fallback: Create a generic SolutionAgent if no specific match is found
        if (agentForTask.isEmpty()) {
            System.out.println("[WARNING] Convenient agent not found, using a fallback solution agent instead.");
            agentForTask = Optional.of(new SolutionAgent(UUID.randomUUID().toString(), propertyMap.get("agentName"), null, null));
        }

        var outputs = new ArrayList<Channel>();

        for (Feature e : action.getOutput()) {
            var name = e.getDeclaredName();
            var type = extractChannelType(e, name);

            outputs.add(new Channel(name, type));
        }

        var newStep = new Step(new AgentTask(
                action.getDeclaredName(),
                propertyMap.get("description"),
                outputs,
                agentForTask.get(),
                outputDependencies
        ));

        previousStep.ifPresent(step -> step.setNextStep(newStep));
        return newStep;
    }

    /**
     * Extract the type of a channel (as a Feature).
     */
    static Optional<String> extractChannelType(Feature e, String name) {
        String s = null;
        List<FeatureTyping> t = e.getOwnedTyping();
        if (t != null && !t.isEmpty()) {
            FeatureTyping t0 = t.getFirst();
            s = t0.getType().getName();
            if (s == null)
                System.out.println("[WARNING] Type not found for channel: " + name);
        }
        return Optional.ofNullable(s);
    }

    /**
     * Recursively traverses the SysML element tree to extract specific metadata
     * (e.g., description and agent name) from redefined features or feature values.
     *
     * @param e           The current SysML element being inspected.
     * @param propertyMap The accumulator map for extracted properties.
     *                    Modifies the property map.
     */
    private static void populateActionProperties(Element e, Map<String, String> propertyMap) {
        // stop recursion if all required fields are found
        if (propertyMap.containsKey("description") && propertyMap.containsKey("agentName")) {
            return;
        }

        var redefinedDescription = false;
        var redefinedAgentName = false;

        for (var i = 0; i < e.getOwnedRelationship().size(); i++) {
            if (e.getOwnedRelationship().get(i) instanceof Redefinition rd) {
                var targetName = UtilAttributeMapper.getSafeName(rd.getRedefinedFeature());
                if (targetName.isPresent()) {
                    if ("description".equals(targetName.get())) {
                        redefinedDescription = true;
                    }
                    if ("agent".equals(targetName.get())) {
                        redefinedAgentName = true;
                    }
                }
            }

            // Extract values if a redefinition is detected
            if (redefinedDescription || redefinedAgentName) {
                var child = i < e.getOwnedRelationship().size() ? e.getOwnedRelationship().get(i + 1) : e.getOwnedRelationship().get(i - 1);
                if (child instanceof FeatureValue fv) {
                    var propertyValue = e.getOwnedElement().getFirst();

                    if (propertyValue instanceof LiteralString ls) {
                        propertyMap.put("description", ls.getValue());
                        return;
                    }
                    if (propertyValue instanceof FeatureReferenceExpression fre) {
                        Optional<String> safeName = UtilAttributeMapper.getSafeName(fre.getReferent());
                        if (safeName.isPresent())
                            propertyMap.put("agentName", safeName.get());
                        else
                            System.out.println("[WARNING] Name not found.");
                        return;
                    }
                }
            }
            // Recursive call on relationships
            populateActionProperties(e.getOwnedRelationship().get(i), propertyMap);
        }

        // Recursive call on child elements
        for (var child : e.getOwnedElement()) {
            populateActionProperties(child, propertyMap);
        }
    }
}