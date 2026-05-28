package org.example.transformer;

import org.example.agents.mosaico.MosaicoAgent;
import org.example.dto.step.Step;
import org.example.transformer.mapper.FlowMapper;
import org.example.transformer.mapper.PartMapper;
import org.omg.sysml.lang.sysml.*;

import java.security.InvalidParameterException;
import java.util.*;

/**
 * The primary entry point for decoding SysML v2 models into executable multi-agent plans.
 * <p>
 * This class handles resource loading, orchestrates the mapping of agents (structural)
 * and flows (behavioral), and produces a unified {@link Step}.
 */
public interface SysMLDecoder {

    /**
     * Decodes a SysML collaboration pattern file into a structured task execution plan.
     *
     * @param collaborationPatternPath The file system path to the .sysml source file.
     * @return A complete {@link Step} ready for the execution engine.
     */
    static Step decode(String collaborationPatternPath) {

        final String libPrefix = "src/main/resources/sysml.library";
        var sysml = new MySysMLUtil(libPrefix);

        sysml.readResource("src/main/resources/mosaico.sysml");
        var sysmlResource = sysml.readResource(collaborationPatternPath);

        if (!sysmlResource.getErrors().isEmpty()) {
            for (var e : sysmlResource.getErrors()) System.out.println("[ERROR] " + e);
            throw new InvalidParameterException("Error reported while reading resource " + collaborationPatternPath);
        }

        var packages = (Namespace) sysmlResource.getContents().getFirst();
        var agentTypes = new HashMap<String, MosaicoAgent>();
        var mosaicoAgents = new ArrayList<MosaicoAgent>();
        var rootFlows = new ArrayList<Element>();

        mapModelResources(packages, agentTypes, mosaicoAgents, rootFlows);

        return FlowMapper.parseSteps(rootFlows, mosaicoAgents);
    }

    /**
     * Recursively traverses the SysML model hierarchy to identify agent definitions,
     * specific agent usages (references), and action succession flows.
     */
    private static void mapModelResources(Element e, Map<String, MosaicoAgent> agents, List<MosaicoAgent> mosaicoAgents, List<Element> rootFlows) {
        // First pass: Process relationships (memberships, successions)
        for (var rel : e.getOwnedRelationship()) {
            mapModelResources(rel, agents, mosaicoAgents, rootFlows);

            // Identify succession flows within Action Definitions
            if (e instanceof ActionDefinition && rel instanceof FeatureMembership fm) {
                var relatedElement = fm.getOwnedRelatedElement().getFirst();
                if (relatedElement instanceof SuccessionAsUsage sau) {
                    rootFlows.add(sau);
                } else if (relatedElement instanceof TransitionUsage tru) {
                    rootFlows.add(tru);
                }
            }
        }

        // Second pass: Process child elements (Parts and References)
        for (var child : e.getOwnedElement()) {
            // Case 1: Discover Agent Definitions (structural templates)
            if (child instanceof PartDefinition partDefinition) {
                PartMapper.mapPartToAgents(partDefinition, agents);
            }

            // Case 2: Discover Agent Usages (instances in a collaboration)
            if (child instanceof ReferenceUsage rf && !(e instanceof ActionUsage)) {
                processReferenceUsage(rf, agents, mosaicoAgents);
            }

            mapModelResources(child, agents, mosaicoAgents, rootFlows);
        }
    }

    /**
     * Resolves a {@link ReferenceUsage} to a concrete agent instance by matching
     * its type against discovered {@link PartDefinition}s.
     */
    private static void processReferenceUsage(ReferenceUsage rf, Map<String, MosaicoAgent> agents, List<MosaicoAgent> mosaicoAgents) {
        for (var childRef : rf.getOwnedRelationship()) {
            if (childRef instanceof FeatureTyping ft) {
                // Get the type name to find the corresponding template
                String agentType = ft.getOwningFeature().getType().getFirst().getDeclaredName();

                if (agents.containsKey(agentType)) {
                    // Clone or reference the template and set the specific instance name
                    var newAgent = agents.get(agentType);
                    newAgent.setName(rf.getDeclaredName());
                    // FIXME  : if the agent already has a name, this setName overwrites the name, and the old name reference is lost.
                    mosaicoAgents.add(newAgent);
                } else {
                    // Fallback: Create a generic agent if the type definition is missing
                    mosaicoAgents.add(PartMapper.createAgent(agentType, UUID.randomUUID().toString(), rf.getDeclaredName(), null, null));
                }
                break;
            }
        }
    }
}