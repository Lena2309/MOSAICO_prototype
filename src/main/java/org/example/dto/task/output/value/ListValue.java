package org.example.dto.task.output.value;

import java.util.ArrayList;
import java.util.List;

public class ListValue extends Value {
    private final String typeList;
    private final List<Value> values;

    public ListValue(String typeList) {
        this.typeList = typeList;
        this.values = new ArrayList<>();
    }

    public ListValue(String typeList, int maxBound) {
        this.typeList = typeList;
        this.values = new ArrayList<>(maxBound);
    }

    public boolean addValue(Value value) {
        return values.add(value);
    }

    public boolean removeValue(Value value) {
        return values.remove(value);
    }

    public List<Value> getValues() {
        return values;
    }

    public String getTypeList() {
        return typeList;
    }

    @Override
    public String print() {
        return "";
    }
}
