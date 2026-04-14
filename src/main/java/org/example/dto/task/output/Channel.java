package org.example.dto.task.output;

import java.util.Optional;

public class Channel {
    private final String name;
    private final String type;
    private final boolean multiple;
    private final int maxBound;

    public Channel(String name, Optional<String> type, boolean multiple, int maxBound) {
        this.name = name;
        this.type = type.orElse(null);
        this.multiple = multiple;
        this.maxBound = maxBound;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getMaxBound() {
        return maxBound;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public boolean isInfinite() {
        return maxBound == 0;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Channel channel)) return false;
        return this.name.equals(channel.name);
    }

    @Override
    public String toString() {
        final String t = (this.type == null ? "(no type)" : "(" + this.type + (this.multiple ? " [0.." + (this.maxBound == 0 ? "*" : this.maxBound) + "]" : "") + ")");
        return "channel:" + name + " " + t;
    }
}
