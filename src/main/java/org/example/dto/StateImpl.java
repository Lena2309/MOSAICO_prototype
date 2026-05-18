package org.example.dto;

import org.example.dto.task.output.TaskOutput;
import org.example.dto.task.output.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class StateImpl extends ArrayList<TaskOutput> implements State {
    HashMap<String, Value> memory = new HashMap<>();

    public StateImpl(){super();}

    @Override
    public void write(String name, Value val) {
        System.out.println("[LOG] Assign " + val + " to " + name);
        this.memory.put(name, val);
        System.out.println("[LOG][LOCAL MEMORY] " + this.memory.toString());
    }

    @Override
    public Optional<Value> getFromMemory(String name) {
        var res = memory.get(name);
        if (res == null) return Optional.empty();
        else return Optional.of(res);
    }
}
