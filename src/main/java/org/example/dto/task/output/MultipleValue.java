package org.example.dto.task.output;

import java.util.ArrayList;
import java.util.List;

public class MultipleValue extends Value {
    final List<Value> values;

    public MultipleValue() {
        this.values = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MultipleValue that)) return false;
        return values.equals(that.values);
    }

    @Override
    public String toString() {
        return "MultipleValues:" +"'" + values + "'";
    }

    public String print() {
        return values.toString();
    }

    public void addValue(Value v){
        this.values.add(v);
    }
}
