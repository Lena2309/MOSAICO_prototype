package org.example.dto.task.output.value;

import java.util.Objects;

public class NullValue extends Value {
    public final String value = null;

    public NullValue() {
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(null);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NullValue;
    }

    @Override
    public String toString() {
        return "Null:" + "'" + value + "'";
    }

    public String print() {
        return String.valueOf(value);
    }
}
