package org.example.parser;

import org.example.agents.MosaicoAgent;
import org.example.dto.TaskExecutionPlan;
import org.example.parser.util.FlowMapper;
import org.example.parser.util.PartMapper;
import org.omg.sysml.lang.sysml.*;

import java.util.*;

public interface SysMLDecoder {
    static TaskExecutionPlan decode(String collaborationPatternPath) {
        final String libPrefix = "org/example/sysml.library";
        var sysml = new MySysMLUtil(libPrefix);
        sysml.readResource("src/main/resources/mosaico.sysml");
        var sysmlResource = sysml.readResource(collaborationPatternPath);

        var packages = (Namespace) sysmlResource.getContents().getFirst();
        var agents = new HashMap<String, MosaicoAgent>();
        var mosaicoAgents = new ArrayList<MosaicoAgent>();
        var rootFlows = new ArrayList<SuccessionAsUsage>();
        mapModelResources(packages, agents, mosaicoAgents, rootFlows);

        return FlowMapper.parseTaskExecutionPlan(rootFlows, mosaicoAgents);
    }

    private static void mapModelResources(Element e, Map<String, MosaicoAgent> agents, List<MosaicoAgent> mosaicoAgents, List<SuccessionAsUsage> rootFlows) {
        for (var rel : e.getOwnedRelationship()) {
            mapModelResources(rel, agents, mosaicoAgents, rootFlows);

            if (e instanceof ActionDefinition && rel instanceof FeatureMembership fm) {
                if (fm.getOwnedRelatedElement().getFirst() instanceof SuccessionAsUsage sau) {
                    rootFlows.add(sau);
                }
            }
        }

        for (var child : e.getOwnedElement()) {
            if (child instanceof PartDefinition partDefinition) {
                PartMapper.mapPartToAgents(partDefinition, agents);
            }
            if (child instanceof ReferenceUsage rf && !(e instanceof ActionUsage)) {
                processReferenceUsage(rf, agents, mosaicoAgents);
            }

            mapModelResources(child, agents, mosaicoAgents, rootFlows);
        }
    }

    private static void processReferenceUsage(ReferenceUsage rf, Map<String, MosaicoAgent> agents, List<MosaicoAgent> mosaicoAgents) {
        for (var childRef : rf.getOwnedRelationship()) {
            if (childRef instanceof FeatureTyping ft) {
                String agentType = ft.getOwningFeature().getType().getFirst().getDeclaredName();

                if (agents.containsKey(agentType)) {
                    var newAgent = agents.get(agentType);
                    newAgent.setName(rf.getDeclaredName());
                    mosaicoAgents.add(newAgent);
                } else {
                    mosaicoAgents.add(PartMapper.createAgent(agentType, UUID.randomUUID().toString(), rf.getDeclaredName(), null, null));
                }
                break;
            }
        }
    }
}
