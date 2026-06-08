package eu.mosaico_project.transformation.mapper;

import eu.mosaico_project.agents.mosaico.MosaicoAgent;
import eu.mosaico_project.dto.conditional.Condition;
import eu.mosaico_project.dto.conditional.LoopKind;
import eu.mosaico_project.dto.conditional.expression.Expression;
import eu.mosaico_project.dto.conditional.expression.ExpressionBuilder;
import eu.mosaico_project.dto.step.IfStep;
import eu.mosaico_project.dto.step.LoopStep;
import eu.mosaico_project.dto.step.ParallelStep;
import eu.mosaico_project.dto.step.Step;
import eu.mosaico_project.dto.task.Task;
import org.omg.sysml.lang.sysml.*;

import java.security.InvalidParameterException;
import java.util.*;

import static eu.mosaico_project.transformation.mapper.UtilAttributeMapper.getSafeName;

public interface FlowMapper {

    static Step parseSteps(List<Element> rootFlows, List<MosaicoAgent> mosaicoAgents) {
        Map<String, Task> taskOutputParameters = new HashMap<>();
        Set<Element> processedNodes = new HashSet<>();
        Map<Element, Step> stepMap = new HashMap<>();

        for (var flow : rootFlows) {
            Element source = getSource(flow);
            Element target = getTarget(flow);

            if (source == null || target == null) continue;

            if (flow instanceof TransitionUsage) {
                getOrProcessNode(source, rootFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);
                continue;
            }

            if (isForkNode(source) || isDecisionNode(source, rootFlows) || isJoinNode(target)) {
                getOrProcessNode(source, rootFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);
                getOrProcessNode(target, rootFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);
                continue;
            }

            Step sourceStep = getOrProcessNode(source, rootFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);
            Step targetStep = getOrProcessNode(target, rootFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);

            if (sourceStep != null && targetStep != null) {
                sourceStep.setNextStep(targetStep);
            }
        }

        return extractHeadStep(rootFlows, stepMap);
    }

    private static Step getOrProcessNode(
            Element e,
            List<Element> scopeFlows,
            Map<String, Task> taskOutputParameters,
            List<MosaicoAgent> mosaicoAgents,
            Set<Element> processedNodes,
            Map<Element, Step> stepMap) {

        if (stepMap.containsKey(e)) return stepMap.get(e);

        if (isStartNode(e) || isDoneNode(e) || isJoinNode(e)) return null;

        if (e instanceof TransitionUsage || e instanceof SuccessionAsUsage) return null;

        if (!processedNodes.add(e)) {
            // System.out.println("[WARNING] Graph cycle detected at node: " + getSafeName(e).orElse("Unknown") + ". Breaking AST recursion.");
            return null;
        }

        Step newStep = null;

        // --- PARALLEL BLOCK HANDLING ---
        if (isForkNode(e)) {
            List<Step> parallelBody = new ArrayList<>();

            var outgoingFlows = scopeFlows.stream()
                    .filter(f -> getSource(f) != null && getSource(f).equals(e))
                    .toList();

            for (var outFlow : outgoingFlows) {
                var branchStart = getTarget(outFlow);
                processBranch(branchStart, scopeFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap, parallelBody);
            }

            newStep = new ParallelStep(parallelBody, Optional.empty());
            stepMap.put(e, newStep);

            Element joinNode = findJoinNode(e, scopeFlows);
            if (joinNode != null) stepMap.put(joinNode, newStep);

            return newStep;
        }

        // --- LOOP HANDLING ---
        if (isLoopNode(e)) {
            newStep = buildSubPlan(e, taskOutputParameters, mosaicoAgents, processedNodes);
            stepMap.put(e, newStep);
            return newStep;
        }

        // --- ACTION & DECISION HANDLING ---
        if (e instanceof ActionUsage actionUsage) {

            // 1. Build the Action Task
            if (!hasSubPlans(actionUsage)) {
                var dependencies = actionUsage.getInput().stream()
                        .map(Element::getDeclaredName)
                        .map(taskOutputParameters::get)
                        .filter(Objects::nonNull)
                        .toList();

                newStep = ActionMapper.mapActionToStep(actionUsage, mosaicoAgents, dependencies, Optional.empty());

                stepMap.put(e, newStep);

                Step finalNewStep = newStep;
                actionUsage.getOutput().stream()
                        .map(Element::getDeclaredName)
                        .forEach(output -> taskOutputParameters.put(output, finalNewStep.getTask()));
            } else {
                newStep = buildSubPlan(e, taskOutputParameters, mosaicoAgents, processedNodes);
                stepMap.put(e, newStep);
            }

            // 2. Synthesize an IfStep if this action also acts as a Decision/Transition Node
            var outgoingTransitions = scopeFlows.stream()
                    .filter(f -> f instanceof TransitionUsage tru && tru.getSource() != null && tru.getSource().equals(e))
                    .map(f -> (TransitionUsage) f)
                    .toList();

            if (!outgoingTransitions.isEmpty()) {
                var thenTransition = outgoingTransitions.stream()
                        .filter(t -> t.getGuardExpression() != null && !t.getGuardExpression().isEmpty())
                        .findFirst().orElse(null);

                var elseTransition = outgoingTransitions.stream()
                        .filter(t -> t.getGuardExpression() == null || t.getGuardExpression().isEmpty())
                        .findFirst().orElse(null);

                Expression exp = null;
                if (thenTransition != null) {
                    exp = ExpressionBuilder.transpile(thenTransition.getGuardExpression().getFirst());
                }

                Step thenStep = null;
                if (thenTransition != null && thenTransition.getTarget() != null && !isJoinNode(thenTransition.getTarget())) {
                    thenStep = getOrProcessNode(thenTransition.getTarget(), scopeFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);
                }

                Optional<Step> elseStep = Optional.empty();
                if (elseTransition != null && elseTransition.getTarget() != null && !isJoinNode(elseTransition.getTarget())) {
                    Step parsedElseStep = getOrProcessNode(elseTransition.getTarget(), scopeFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);
                    elseStep = Optional.ofNullable(parsedElseStep);
                }

                IfStep ifStep = new IfStep(thenStep, elseStep, new Condition(exp), Optional.empty());

                // Link the ActionStep directly into the new IfStep
                newStep.setNextStep(ifStep);

                Element joinNode = findJoinNode(e, scopeFlows);
                if (joinNode != null) stepMap.put(joinNode, ifStep);
            }

            return newStep;
        }

        // --- PURE CONTROL DECISION NODE HANDLING ---
        // Fallback for explicit SysML DecisionNodes that aren't ActionUsages
        if (isDecisionNode(e, scopeFlows)) {
            var outgoingTransitions = scopeFlows.stream()
                    .filter(f -> f instanceof TransitionUsage tru && tru.getSource() != null && tru.getSource().equals(e))
                    .map(f -> (TransitionUsage) f)
                    .toList();

            var thenTransition = outgoingTransitions.stream()
                    .filter(t -> t.getGuardExpression() != null && !t.getGuardExpression().isEmpty())
                    .findFirst().orElse(null);

            var elseTransition = outgoingTransitions.stream()
                    .filter(t -> t.getGuardExpression() == null || t.getGuardExpression().isEmpty())
                    .findFirst().orElse(null);

            Expression exp = null;
            if (thenTransition != null) {
                exp = ExpressionBuilder.transpile(thenTransition.getGuardExpression().getFirst());
            }

            Step thenStep = null;
            if (thenTransition != null && thenTransition.getTarget() != null && !isJoinNode(thenTransition.getTarget())) {
                thenStep = getOrProcessNode(thenTransition.getTarget(), scopeFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);
            }

            Optional<Step> elseStep = Optional.empty();
            if (elseTransition != null && elseTransition.getTarget() != null && !isJoinNode(elseTransition.getTarget())) {
                Step parsedElseStep = getOrProcessNode(elseTransition.getTarget(), scopeFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);
                elseStep = Optional.ofNullable(parsedElseStep);
            }

            newStep = new IfStep(thenStep, elseStep, new Condition(exp), Optional.empty());
            stepMap.put(e, newStep);

            Element joinNode = findJoinNode(e, scopeFlows);
            if (joinNode != null) stepMap.put(joinNode, newStep);

            return newStep;
        }

        return null;
    }

    private static Step buildSubPlan(
            Element e,
            Map<String, Task> taskOutputParameters,
            List<MosaicoAgent> mosaicoAgents,
            Set<Element> processedNodes) {

        var internalFlows = getInternalFlows(e);
        Map<Element, Step> localStepMap = new HashMap<>();

        for (var flow : internalFlows) {
            Element source = getSource(flow);
            Element target = getTarget(flow);

            if (source == null || target == null) continue;

            if (flow instanceof TransitionUsage) {
                getOrProcessNode(source, internalFlows, taskOutputParameters, mosaicoAgents, processedNodes, localStepMap);
                continue;
            }

            if (isForkNode(source) || isDecisionNode(source, internalFlows) || isJoinNode(target)) {
                getOrProcessNode(source, internalFlows, taskOutputParameters, mosaicoAgents, processedNodes, localStepMap);
                getOrProcessNode(target, internalFlows, taskOutputParameters, mosaicoAgents, processedNodes, localStepMap);
                continue;
            }

            getOrProcessNode(source, internalFlows, taskOutputParameters, mosaicoAgents, processedNodes, localStepMap);
            getOrProcessNode(target, internalFlows, taskOutputParameters, mosaicoAgents, processedNodes, localStepMap);
        }

        var headStep = extractHeadStep(internalFlows, localStepMap);

        Condition condition = null;
        LoopKind kind = null;
        if (e instanceof WhileLoopActionUsage loopActionUsage) {
            if (loopActionUsage.getUntilArgument() != null) {
                kind = LoopKind.UNTIL;
                condition = new Condition(ExpressionBuilder.transpile(loopActionUsage.getUntilArgument()));
            } else if (loopActionUsage.getWhileArgument() != null) {
                kind = LoopKind.WHILE;
                condition = new Condition(ExpressionBuilder.transpile(loopActionUsage.getWhileArgument()));
            }
            return new LoopStep(headStep, condition, kind, Optional.empty());
        } else if (e instanceof ActionUsage) {
            return headStep;
        } else throw new InvalidParameterException("Not handled by the flow mapper: " + e.getClass());
    }

    private static void processBranch(
            Element current,
            List<Element> scopeFlows,
            Map<String, Task> taskOutputParameters,
            List<MosaicoAgent> mosaicoAgents,
            Set<Element> processedNodes,
            Map<Element, Step> stepMap,
            List<Step> branchTasks) {

        if (isJoinNode(current)) return;

        Step currentStep = getOrProcessNode(current, scopeFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);
        if (currentStep != null && !branchTasks.contains(currentStep)) {
            branchTasks.add(currentStep);
        }

        var nextElements = scopeFlows.stream()
                .filter(f -> getSource(f) != null && getSource(f).equals(current))
                .map(FlowMapper::getTarget)
                .filter(Objects::nonNull)
                .toList();

        for (var next : nextElements) {
            if (isJoinNode(next)) continue;

            Step nextStep = getOrProcessNode(next, scopeFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap);
            if (currentStep != null && nextStep != null) {
                currentStep.setNextStep(nextStep);
            }
            processBranch(next, scopeFlows, taskOutputParameters, mosaicoAgents, processedNodes, stepMap, branchTasks);
        }
    }

    /**
     * Traverses the flows topologically to generate the linked sequence of Steps,
     * returning the head of the chain.
     */
    private static Step extractHeadStep(List<Element> flows, Map<Element, Step> stepMap) {
        List<Step> ordered = new ArrayList<>();

        Element current = flows.stream()
                .map(FlowMapper::getSource)
                .filter(Objects::nonNull)
                .filter(source -> flows.stream().noneMatch(f -> getTarget(f) != null && getTarget(f).equals(source)))
                .findFirst()
                .orElse(null);

        if (current == null && !flows.isEmpty()) {
            current = getSource(flows.getFirst());
        }

        Set<Element> visited = new HashSet<>();
        while (current != null && visited.add(current)) {
            Step s = stepMap.get(current);

            if (s != null && !ordered.contains(s)) ordered.add(s);

            if (isForkNode(current) || isDecisionNode(current, flows)) {
                current = findJoinNode(current, flows);
                if (current == null) break;
            }

            Element finalCurrent = current;
            var nextFlow = flows.stream()
                    .filter(f -> getSource(f) != null && getSource(f).equals(finalCurrent))
                    .findFirst();

            current = nextFlow.map(FlowMapper::getTarget).orElse(null);
        }

        if (ordered.isEmpty()) {
            return null;
        }

        for (int i = 0; i < ordered.size() - 1; i++) {
            ordered.get(i).setNextStep(ordered.get(i + 1));
        }

        return ordered.getFirst();
    }

    private static Element findJoinNode(Element forkStart, List<Element> flows) {
        Queue<Element> queue = new LinkedList<>();
        Set<Element> visited = new HashSet<>();
        queue.add(forkStart);

        while (!queue.isEmpty()) {
            Element curr = queue.poll();
            if (isJoinNode(curr)) return curr;

            if (visited.add(curr)) {
                var nexts = flows.stream()
                        .filter(f -> getSource(f) != null && getSource(f).equals(curr))
                        .map(FlowMapper::getTarget)
                        .filter(Objects::nonNull)
                        .toList();
                queue.addAll(nexts);
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------------
    //                            NODE TYPE CHECKS & UTILS
    // -----------------------------------------------------------------------------

    private static Element getSource(Element flow) {
        if (flow instanceof SuccessionAsUsage sau && !sau.getSource().isEmpty()) return sau.getSource().getFirst();
        if (flow instanceof TransitionUsage tru) return tru.getSource();
        return null;
    }

    private static Element getTarget(Element flow) {
        if (flow instanceof SuccessionAsUsage sau && !sau.getTarget().isEmpty()) return sau.getTarget().getFirst();
        if (flow instanceof TransitionUsage tru) return tru.getTarget();
        return null;
    }

    private static boolean isDecisionNode(Element e, List<Element> flows) {
        return flows.stream().anyMatch(f -> f instanceof TransitionUsage tru && tru.getSource() != null && tru.getSource().equals(e));
    }

    private static boolean isStartNode(Element e) {
        var n = getSafeName(e);
        return (n.isPresent() && "start".equals(n.get())) || "InitialNode".equals(e.eClass().getName());
    }

    private static boolean isDoneNode(Element e) {
        var n = getSafeName(e);
        return (n.isPresent() && "done".equals(n.get())) || "ActivityFinalNode".equals(e.eClass().getName());
    }

    private static boolean isForkNode(Element e) {
        var n = getSafeName(e);
        return e instanceof ForkNode || (n.isPresent() && "forkNode".equals(n.get()));
    }

    private static boolean isJoinNode(Element e) {
        var n = getSafeName(e);
        return e instanceof JoinNode || (n.isPresent() && "joinNode".equals(n.get()));
    }

    private static boolean isLoopNode(Element e) {
        var n = getSafeName(e);
        return e instanceof LoopActionUsage || (n.isPresent() && "loopAction".equals(n.get()));
    }

    private static boolean hasSubPlans(Element e) {
        return !getInternalFlows(e).isEmpty();
    }

    private static List<Element> getInternalFlows(Element e) {
        List<Element> internalFlows = new ArrayList<>();
        if (e == null)
            throw new InvalidParameterException("Empty element.");

        for (var rel : e.getOwnedRelationship()) {
            if (rel instanceof FeatureMembership fm) {
                for (var subChild : fm.getRelatedElement()) {
                    if (subChild == e) continue;

                    if (subChild instanceof SuccessionAsUsage flow) {
                        internalFlows.add(flow);
                    } else if (subChild instanceof TransitionUsage tru) {
                        internalFlows.add(tru);
                    } else if (isLoopNode(e) && subChild instanceof ActionUsage innerAction) {
                        if (!isLoopNode(innerAction)) {
                            internalFlows.addAll(getInternalFlows(innerAction));
                        }
                    }
                }
            } else System.out.println("[WARNING] Owned Relationship not handled: " + rel);
        }
        return internalFlows;
    }
}