package org.example.parser;

import org.example.agents.MosaicoAgent;
import org.example.dto.TaskExecutionPlan;
import org.example.parser.util.FlowMapper;
import org.example.parser.util.PartMapper;
import org.omg.sysml.lang.sysml.*;

import java.util.*;

/**
 * The primary entry point for decoding SysML v2 models into executable multi-agent plans.
 * <p>
 * This class handles resource loading, orchestrates the mapping of agents (structural)
 * and flows (behavioral), and produces a unified {@link TaskExecutionPlan}.
 */
public interface SysMLDecoder {

    /**
     * Decodes a SysML collaboration pattern file into a structured task execution plan.
     *
     * @param collaborationPatternPath The file system path to the .sysml source file.
     * @return A complete {@link TaskExecutionPlan} ready for the execution engine.
     */
    static TaskExecutionPlan decode(String collaborationPatternPath) {

        // to load pre-defined types
        //final String libPrefix = "org/example/sysml.library";
        final String libPrefix = "src/main/resources/sysml.library";
        // Initialize SysML utility with custom library paths
        var sysml = new MySysMLUtil(libPrefix);

        // Load the core Mosaico profile/library followed by the specific collaboration pattern
        sysml.readResource("src/main/resources/mosaico.sysml");
        var sysmlResource = sysml.readResource(collaborationPatternPath);

        var packages = (Namespace) sysmlResource.getContents().getFirst();
        var agentTypes = new HashMap<String, MosaicoAgent>();
        var mosaicoAgents = new ArrayList<MosaicoAgent>();
        var rootFlows = new ArrayList<SuccessionAsUsage>();

        // Recursively traverse the model to populate agentTypes and control flows
        mapModelResources(packages, agentTypes, mosaicoAgents, rootFlows);

        return FlowMapper.parseTaskExecutionPlan(rootFlows, mosaicoAgents);
    }

    /**
     * Recursively traverses the SysML model hierarchy to identify agent definitions,
     * specific agent usages (references), and action succession flows.
     */
    private static void mapModelResources(Element e, Map<String, MosaicoAgent> agents, List<MosaicoAgent> mosaicoAgents, List<SuccessionAsUsage> rootFlows) {
        // First pass: Process relationships (memberships, successions)
        for (var rel : e.getOwnedRelationship()) {
            mapModelResources(rel, agents, mosaicoAgents, rootFlows);

            // Identify succession flows within Action Definitions
            if (e instanceof ActionDefinition && rel instanceof FeatureMembership fm) {
                if (fm.getOwnedRelatedElement().getFirst() instanceof SuccessionAsUsage sau) {
                    rootFlows.add(sau);
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