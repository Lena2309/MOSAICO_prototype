package org.example.agents;

public class BooleanValue extends Value {
    @Override
    public String toString() {
        return "BooleanValue{" +
                "value=" + value +
                '}';
    }

    final boolean value ;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public boolean value() { return this.value; }
}
