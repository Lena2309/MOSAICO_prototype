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
}
