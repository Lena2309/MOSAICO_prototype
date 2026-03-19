package org.example.dto;

import java.util.List;

public abstract class Expression {
    abstract boolean checkCondition(List<TaskOutput> trace);
}
