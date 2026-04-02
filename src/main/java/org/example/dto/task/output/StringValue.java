package org.example.dto.task.output;

public class StringValue extends Value {
    final String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StringValue that)) return false;
        return value.equals(that.value);
    }

    @Override
    public String toString() {
        return "StringValue{" +
                "value='" + value + '\'' +
                '}';
    }

    public String print() {
        return value;
    }
}
