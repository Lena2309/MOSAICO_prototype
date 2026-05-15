package org.example.dto;

import org.example.dto.task.output.TaskOutput;

import java.util.ArrayList;
import java.util.List;

public class StateImpl extends ArrayList<TaskOutput> implements State {
    public StateImpl(){super();}
    public StateImpl(List l){super(l);}
}
