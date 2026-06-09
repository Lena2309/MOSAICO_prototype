package eu.mosaico_project.transformation.mapper;

import eu.mosaico_project.agents.mosaico.MosaicoAgent;
import eu.mosaico_project.agents.mosaico.ReferenceAgent;
import eu.mosaico_project.agents.mosaico.SolutionAgent;
import eu.mosaico_project.miol.Assignment;
import eu.mosaico_project.miol.Statement;
import eu.mosaico_project.miol.conditional.expression.ExpressionBuilder;
import eu.mosaico_project.miol.step.Step;
import eu.mosaico_project.miol.task.AgentTask;
import eu.mosaico_project.miol.task.AlgorithmicTask;
import eu.mosaico_project.miol.task.Task;
import eu.mosaico_project.miol.task.output.Channel;
import org.omg.sysml.lang.sysml.*;

import java.security.InvalidParameterException;
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
        return mapActionToAlgorithmicTask(action, outputDependencies, previousStep);
    }

    private static Step mapActionToAlgorithmicTask(ActionUsage action, List<Task> outputDependencies, Optional<Step> previousStep) {
        Map<String, String> propertyMap = new HashMap<>();
        populateActionProperties(action, propertyMap);

        var inputs = new ArrayList<Channel>();
        var outputs = new ArrayList<Channel>();
        populateInputAndOutputDependencies(action, inputs, outputs, outputDependencies);

        Step newStep = new Step(new AlgorithmicTask(
                buildName(action),
                propertyMap.get("description"),
                outputs,
                inputs,
                outputDependencies,
                extractParents(action),
                extractStatement(action)
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

            for (var supertype : action.allSupertypes()) {
                if (Objects.equals(supertype.getDeclaredName(), "ReferenceStep")) {
                    agentForTask = Optional.of(new ReferenceAgent(UUID.randomUUID().toString(), "default-reference-agent", "Default Reference agent.", null));
                    break;
                } else if (Objects.equals(supertype.getDeclaredName(), "ConsensusStep")) {
                    agentForTask = Optional.of(new ReferenceAgent(UUID.randomUUID().toString(), "default-consensus-agent", "Default Consensus agent.", null));
                    break;
                } else if (Objects.equals(supertype.getDeclaredName(), "SupervisionAgent")) {
                    agentForTask = Optional.of(new ReferenceAgent(UUID.randomUUID().toString(), "default-supervision-agent", "Default Supervision agent.", null));
                    break;
                }
            }

            if (agentForTask.isEmpty())
                agentForTask = Optional.of(new SolutionAgent(UUID.randomUUID().toString(), "default-solution-agent", "Default Solution agent.", null));
        }

        var inputs = new ArrayList<Channel>();
        var outputs = new ArrayList<Channel>();
        populateInputAndOutputDependencies(action, inputs, outputs, outputDependencies);

        var newStep = new Step(new AgentTask(
                buildName(action),
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

        // First populate inputs, only if this task comes after another task.
        if (!(outputDependencies.isEmpty())) {
            for (var e : action.getInput()) {

                // Look for existing output channels in previous tasks.
                String requiredName = e.getDeclaredName();
                var inputFound = false;
                for (var task : outputDependencies) {
                    for (var channel : task.getOutputChannels()) {
                        if (Objects.equals(channel.name(), requiredName)) {
                            inputs.add(channel);
                            inputFound = true;
                            break;
                        }
                    }
                    if (inputFound) break;
                }
            }
        }

        // Build new channels for outputs.
        for (Feature e : action.getOutput()) {
            Channel c = buildChannel(e);
            outputs.add(c);
        }

    }

    static Channel buildChannel(Feature e) {
        var name = e.getDeclaredName();
        var type = extractChannelType(e, name);
        var multi = e.getMultiplicity();
        int maxBound = 0;
        if (multi != null)
            maxBound = multi.getOwnedRelationship().getLast() instanceof LiteralInteger li ? li.getValue() : 0;

        return new Channel(name, type, multi != null, maxBound);
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


    static Statement extractStatement(ActionUsage action) {
        if (action instanceof AssignmentActionUsage a)
            return extractAssignment(a);
        else {
            Element el = action.getOwnedRelationship().getFirst().getOwnedRelatedElement().getFirst(); // FIXME
            if (el instanceof ActionUsage a)
                return extractStatement(a);
            else throw new InvalidParameterException("Cannot handle statement " + el.getClass().getSimpleName());
        }
    }

    static Statement extractAssignment(AssignmentActionUsage a) {
        var lhs = UtilAttributeMapper.getSafeName(a.getReferent());
        if (lhs.isEmpty())
            throw new InvalidParameterException("Assignment cannot be resolved.");
        var rhs = ExpressionBuilder.transpile(a.getValueExpression());
        return new Assignment(lhs.get(), rhs); // FIXME
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
        }
        if (s == null)
            System.out.println("[WARNING] Type not found for channel: " + name);
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

    static String buildName(ActionUsage a) {
        final var name = a.getDeclaredName();
        return (name != null ? name : a.path());
    }
}