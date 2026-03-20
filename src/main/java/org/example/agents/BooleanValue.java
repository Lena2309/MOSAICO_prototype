package org.example.agents;

import java.util.Objects;

public class BooleanValue extends Value {
    @Override
    public String toString() {
        return "BooleanValue{" +
                "value=" + value +
                '}';
    }

    final boolean value ;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BooleanValue that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public boolean value() { return this.value; }
}
