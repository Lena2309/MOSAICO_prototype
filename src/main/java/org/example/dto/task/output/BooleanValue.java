package org.example.dto.task.output;

import java.util.Objects;

public class BooleanValue extends Value {
    final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BooleanValue that)) return false;
        return value == that.value;
    }

    @Override
    public String toString() {
        return "BooleanValue{" +
                "value=" + value +
                '}';
    }

    public boolean value() {
        return this.value;
    }

    public String print() {
        return value ? "true" : "false";
    }
}
