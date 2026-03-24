package org.example.agents;

public class StringValue extends Value {
    public StringValue(String value) {
        this.value = value;
    }

    final String value ;

    @Override
    public String toString() {
        return "StringValue{" +
                "value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StringValue that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
