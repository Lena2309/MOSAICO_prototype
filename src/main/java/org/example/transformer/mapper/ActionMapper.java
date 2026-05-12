package org.example.transformer.mapper;

import org.example.agents.mosaico.MosaicoAgent;
import org.example.agents.mosaico.SolutionAgent;
import org.example.dto.step.AssignmentStep;
import org.example.dto.step.Step;
import org.example.dto.task.AgentTask;
import org.example.dto.task.AssignmentTask;
import org.example.dto.task.Task;
import org.example.dto.task.output.Channel;
import org.omg.sysml.lang.sysml.*;

import java.util.*;

/**
 * Utility interface responsible for mapping SysML {@link ActionUsage} elements
 * to execution-ready {@link Step} objects.
 */
public interface ActionMapper {

    static Step mapActionToStep(ActionUsage action, List<MosaicoAgent> mosaicoAgents, List<Task> outputDependencies, Optional<Step> previousStep) {
        for (var supertype : action.allSupertypes()) {
            if (Objects.equals(supertype.getDeclaredName(), "AgentStep")) {
                return mapActionToAgentTask(action, mosaicoAgents, outputDependencies, previousStep);
            }
        }
        return mapActionToTask(action, outputDependencies, previousStep);
    }

    private static Step mapActionToTask(ActionUsage action, List<Task> outputDependencies, Optional<Step> previousStep) {
        Map<String, String> propertyMap = new HashMap<>();
        populateActionProperties(action, propertyMap);

        var inputs = new ArrayList<Channel>();
        var outputs = new ArrayList<Channel>();
        populateInputAndOutputDependencies(action, inputs, outputs, outputDependencies);

        var newStep = new AssignmentStep(new AssignmentTask(
                action.getDeclaredName(),
                propertyMap.get("description"),
                outputs,
                inputs,
                outputDependencies,
                extractParents(action),
                null // FIXME : Expression to do
        ));

        previousStep.ifPresent(step -> step.setNextStep(newStep));
        return newStep;
    }

    /**
     * Maps a SysML ActionUsage to a domain Task by resolving its properties and associated agent.
     *
     * @param action             The SysML action usage to be parsed.
     * @param mosaicoAgents      List of currently available agents to match against.
     * @param outputDependencies Tasks that must be completed before this task can execute.
     * @return A populated {@link Step} instance.
     */
    private static Step mapActionToAgentTask(ActionUsage action, List<MosaicoAgent> mosaicoAgents, List<Task> outputDependencies, Optional<Step> previousStep) {
        Map<String, String> propertyMap = new HashMap<>();
        populateActionProperties(action, propertyMap);

        var finalAgentName = propertyMap.get("agent");
        if (finalAgentName == null)
            System.out.println("[WARNING] No agent specification found for task: " + action.getDeclaredName() + ".");

        // Attempt to find an existing agent that matches the requirement
        var agentForTask = mosaicoAgents.stream()
                .filter(agent -> Objects.equals(finalAgentName, agent.getName()))
                .findFirst();

        // Fallback: Create a generic SolutionAgent if no specific match is found
        if (agentForTask.isEmpty()) {
            if (finalAgentName != null)
                System.out.println("[ERROR] Convenient agent not found despite being specified, using a fallback solution agent instead. (" + finalAgentName + ")");
            agentForTask = Optional.of(new SolutionAgent(UUID.randomUUID().toString(), propertyMap.get("agent"), null, null));
        }

        var inputs = new ArrayList<Channel>();
        var outputs = new ArrayList<Channel>();
        populateInputAndOutputDependencies(action, inputs, outputs, outputDependencies);

        var newStep = new Step(new AgentTask(
                action.getDeclaredName(),
                propertyMap.get("description"),
                outputs,
                agentForTask.get(),
                inputs,
                outputDependencies,
                extractParents(action),
                propertyMap
        ));

        previousStep.ifPresent(step -> step.setNextStep(newStep));
        return newStep;
    }

    private static void populateInputAndOutputDependencies(ActionUsage action, ArrayList<Channel> inputs, ArrayList<Channel> outputs, List<Task> outputDependencies) {
        if (!(outputDependencies.isEmpty())) {
            for (var e : action.getInput()) {
                var inputFound = false;
                for (var task : outputDependencies) {
                    for (var channel : task.getOutputChannels()) {
                        if (Objects.equals(channel.name(), e.getDeclaredName())) {
                            inputs.add(channel);
                            inputFound = true;
                            break;
                        }
                    }
                    if (inputFound) break;
                }
            }
        }

        for (Feature e : action.getOutput()) {
            var name = e.getDeclaredName();
            var type = extractChannelType(e, name);
            var multi = e.getMultiplicity();
            int maxBound = 0;
            if (multi != null)
                maxBound = multi.getOwnedRelationship().getLast() instanceof LiteralInteger li ? li.getValue() : 0;

            outputs.add(new Channel(name, type.get(), multi != null, maxBound));
        }
    }

    // Fill the parents for absolute names of channels.
    private static List<String> extractParents(ActionUsage action) {
        List<String> parents = new ArrayList<>();
        Element tmp = action;
        while (tmp.getOwner() != null && tmp.getOwner().getDeclaredName() != null) {
            parents.add(tmp.getOwner().getDeclaredName());
            tmp = tmp.getOwner();
        }
        return parents;
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
        var redefinedDescription = false;
        var redefinedAgentName = false;
        var redefinedSeparator = false;

        for (var i = 0; i < e.getOwnedRelationship().size(); i++) {
            Relationship currentRelationship = e.getOwnedRelationship().get(i);
            if (currentRelationship instanceof Redefinition rd) {
                var targetName = UtilAttributeMapper.getSafeName(rd.getRedefinedFeature());
                if (targetName.isPresent()) {
                    switch (targetName.get()) {
                        case "description":
                        case "agent":
                        case "separator": {
                            // Extract values
                            final var child = i < e.getOwnedRelationship().size() ? e.getOwnedRelationship().get(i + 1) : e.getOwnedRelationship().get(i - 1);
                            if (child instanceof FeatureValue) {
                                var propertyValue = e.getOwnedElement().getFirst();

                                if (propertyValue instanceof LiteralString ls) {
                                    propertyMap.put(targetName.get(), ls.getValue());
                                    return;
                                }
                                if (propertyValue instanceof FeatureReferenceExpression fre) {
                                    Optional<String> safeName = UtilAttributeMapper.getSafeName(fre.getReferent());
                                    if (safeName.isPresent())
                                        propertyMap.put(targetName.get(), safeName.get());
                                    else
                                        System.out.println("[WARNING] Name not found.");
                                    return;
                                }
                            }
                        }

                    }
                }
            }
            // Recursive call on relationships
            populateActionProperties(currentRelationship, propertyMap);
        }

        // Recursive call on child elements
        for (var child : e.getOwnedElement()) {
            populateActionProperties(child, propertyMap);
        }
    }
}