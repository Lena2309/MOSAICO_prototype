package org.example.parser;

import org.example.agents.MosaicoAgent;
import org.example.dto.*;
import org.example.dto.WorkflowType;

import java.util.List;

import java.util.*;
import java.util.regex.*;

public class SysMLParser {

    // Regex patterns for extracting specific metadata
    private static final Pattern AGENT_DECLARATION = Pattern.compile("(\\w+)\\s*:\\s*(\\w+);");
    private static final Pattern ACTION_PATTERN = Pattern.compile("(then\\s+)?(loop\\s+)?action\\s+(\\w+)");
    private static final Pattern FORK_PATTERN = Pattern.compile("then\\s+fork;");
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("(::>|redefines)?\\s*description\\s*=\\s*\"([^\"]*)\"");
    private static final Pattern AGENT_ASSIGNMENT = Pattern.compile("(::>|redefines)?\\s*agent\\s*=\\s*(\\w+);");
    private static final Pattern OUT_PARAM = Pattern.compile("out\\s+(\\w+)\\s*:\\s*\\w+");

    // Global registry to resolve agent names found in actions to MosaicoAgent objects
    private final Map<String, MosaicoAgent> agentRegistry = new HashMap<>();

    public static TaskExecutionPlan parse(String collaborationPattern) {
        SysMLParser parser = new SysMLParser();
        return parser.generatePlan(collaborationPattern);
    }

    private TaskExecutionPlan generatePlan(String collaborationPattern) {
        //parseAgents(collaborationPattern);

        var tasks = new ArrayList<Task>();
        var plans = new ArrayList<TaskExecutionPlan>();
        int orderCounter = 1;

        String mainBody = extractBlock(collaborationPattern, "action def");

        if (mainBody == null || mainBody.isEmpty()) {
            return new TaskExecutionPlan(0, List.of(), List.of(), WorkflowType.SEQUENTIAL);
        }

        int cursor = 0;
        while (cursor < mainBody.length()) {
            int actionIndex = mainBody.indexOf("action", cursor);
            int forkIndex = mainBody.indexOf("fork;", cursor);
            int loopIndex = mainBody.indexOf("loop action", cursor);

            int nextEvent = findNextEvent(loopIndex, actionIndex, forkIndex);
            if (nextEvent == -1) break;

            if (nextEvent == loopIndex) {
                // HANDLE LOOP
                String block = extractBlock(mainBody.substring(loopIndex), "loop action");
                // TODO: Improve sub task plans in loop
                List<Task> loopTasks = parseTasksInsideBlock(block); // Recursively parse actions inside loop
                plans.add(new TaskExecutionPlan(orderCounter++, loopTasks, List.of(), WorkflowType.LOOP));
                cursor = loopIndex + block.length() + "loop action".length();
                cursor = mainBody.indexOf(";", cursor) + 1;

            } else if (nextEvent == forkIndex) {
                // HANDLE FORK
                int joinIndex = mainBody.indexOf("then join joinNode;", forkIndex);
                if (joinIndex > 0) {
                    String forkBlock = mainBody.substring(forkIndex, joinIndex);
                    List<Task> parallelTasks = parseForkBlock(forkBlock);
                    plans.add(new TaskExecutionPlan(orderCounter++, parallelTasks, List.of(), WorkflowType.PARALLEL));
                    cursor = joinIndex + "then join joinNode;".length();
                } else {
                    cursor = forkIndex + 10;
                }

            } else {
                // HANDLE SEQUENTIAL ACTION
                String block = extractBlock(mainBody.substring(actionIndex), "action");
                if (block != null) {
                    Task task = parseSingleTask(orderCounter++, block);
                    tasks.add(task);
                    cursor = mainBody.indexOf(block, actionIndex) + block.length();
                } else {
                    cursor++; // Safety skip
                }
            }
        }

        return new TaskExecutionPlan(0, tasks, plans, WorkflowType.SEQUENTIAL);
    }

    // -----------------------------------------------------------------------
    //                      PARSE HELPERS
    // -----------------------------------------------------------------------

    private void parseAgents(String collaborationPattern) {
        // Find lines like: requirementManagerAgent : RequirementManagerAgent;
        Matcher m = AGENT_DECLARATION.matcher(collaborationPattern);
        while (m.find()) {
            String varName = m.group(1);
            String className = m.group(2);
            agentRegistry.put(varName, null);
        }
    }

    private Task parseSingleTask(int executionOrder, String actionBlock) {
        String description = "";

        Matcher descMatcher = DESCRIPTION_PATTERN.matcher(actionBlock);
        if (descMatcher.find()) {
            description = descMatcher.group(2).trim();
        }

        // TODO: gérer les agents
        Matcher agentMatcher = AGENT_ASSIGNMENT.matcher(actionBlock);

        // TODO: gérer les outputs et dependencies
        Matcher outMatcher = OUT_PARAM.matcher(actionBlock);
        String output = "";
        while (outMatcher.find()) {
            output = outMatcher.group(1).trim();
        }

        return new Task(executionOrder, description, null, List.of());
    }

    private List<Task> parseTasksInsideBlock(String block) {
        // Looks for nested actions inside a loop or block
        List<Task> tasks = new ArrayList<>();
        int cursor = 0;
        int orderCounter = 1;
        while (true) {
            int idx = block.indexOf("action ", cursor);
            if (idx == -1) break;

            String innerBlock = extractBlock(block.substring(idx), "action");
            if (innerBlock == null) break;

            tasks.add(parseSingleTask(orderCounter++, innerBlock));
            cursor = idx + innerBlock.length() + 1;
        }
        return tasks;
    }

    private List<Task> parseForkBlock(String forkContent) {
        return parseTasksInsideBlock(forkContent);
    }

    private int findNextEvent(int a, int b, int c) {
        int min = Integer.MAX_VALUE;
        if (a > -1) min = Math.min(min, a);
        if (b > -1) min = Math.min(min, b);
        if (c > -1) min = Math.min(min, c);
        return (min == Integer.MAX_VALUE) ? -1 : min;
    }

    private String extractBlock(String text, String startKeyword) {
        int keywordIdx = text.indexOf(startKeyword);
        if (keywordIdx == -1) return null;

        int openBrace = text.indexOf("{", keywordIdx);
        if (openBrace == -1) return null;

        int depth = 0;
        for (int i = openBrace; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;

            if (depth == 0) {
                return text.substring(openBrace + 1, i);
            }
        }
        return null;
    }
}
