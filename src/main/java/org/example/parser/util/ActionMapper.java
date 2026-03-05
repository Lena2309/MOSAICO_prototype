package org.example.parser.util;

import org.example.agents.MosaicoAgent;
import org.example.agents.SolutionAgent;
import org.example.dto.Task;
import org.omg.sysml.lang.sysml.*;

import java.util.*;

public interface ActionMapper {

    static Task mapActionToTask(ActionUsage action, List<MosaicoAgent> mosaicoAgents, List<Task> outputDependencies, int executionOrder) {
        var propertyMap = new HashMap<String, String>();
        propertyMap = (HashMap<String, String>) findActionProperties(action, propertyMap);

        var finalAgentName = propertyMap.get("agentName");

        var agentForTask = mosaicoAgents.stream()
                .filter(agent -> Objects.equals(finalAgentName, agent.getName()))
                .findFirst();

        if  (agentForTask.isEmpty()) {
            agentForTask = Optional.of(new SolutionAgent(UUID.randomUUID().toString(), propertyMap.get("agentName"), null, null));
        }

        return new Task(
                executionOrder,
                action.getDeclaredName(),
                propertyMap.get("description"),
                agentForTask.get(),
                outputDependencies
        );
    }

    private static Map<String, String> findActionProperties(Element e, Map<String, String> propertyMap) {
        if (propertyMap.containsKey("description") && propertyMap.containsKey("agentName")) {
            return propertyMap;
        }

        var redefinedDescription = false;
        var redefinedAgentName = false;
        for (var i=0 ; i < e.getOwnedRelationship().size() ; i++) {
            if (e.getOwnedRelationship().get(i) instanceof Redefinition rd) {
                var targetName = UtilAttributeMapper.getSafeName(rd.getRedefinedFeature());
                if ("description".equals(targetName)) {
                    redefinedDescription = true;
                }
                if ("agent".equals(targetName)) {
                    redefinedAgentName = true;
                }
            }
            if (redefinedDescription || redefinedAgentName) {
                var child = i < e.getOwnedRelationship().size() ? e.getOwnedRelationship().get(i+1) : e.getOwnedRelationship().get(i-1);
                if (child instanceof FeatureValue fv) {
                    var propertyValue = e.getOwnedElement().getFirst();
                    if (propertyValue instanceof LiteralString ls) {
                        propertyMap.put("description",  ls.getValue());
                        return propertyMap;
                    }
                    if (propertyValue instanceof FeatureReferenceExpression fre) {
                        propertyMap.put("agentName",  UtilAttributeMapper.getSafeName(fre.getReferent()));
                        return propertyMap;
                    }
                }
            }
            findActionProperties(e.getOwnedRelationship().get(i) , propertyMap);
        }

        for (var child :  e.getOwnedElement()) {
            findActionProperties(child, propertyMap);
        }
        return propertyMap;
    }
}
