package org.example.dto.task.output.value;

import java.util.Objects;

public class IntegerValue extends Value {
    public final int value;

    public IntegerValue(int value) {
        this.value = value;
    }

    public IntegerValue(String value) {
        this.value = Integer.parseInt(value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntegerValue that)) return false;
        return value == that.value;
    }

    @Override
    public String toString() {
        return "Integer:" + "'" + value + "'";
    }

    public String print() {
        return String.valueOf(value);
    }

}