package org.example.dto.task.output;

import java.util.Optional;

public record Channel(String name, Optional<String> type, boolean multiple, int maxBound) {
    public boolean isInfinite() {
        return maxBound == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Channel channel)) return false;
        return this.name.equals(channel.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        final String t = (this.type == null ? "(no type)" : "(" + this.type + (this.multiple ? " [0.." + (this.maxBound == 0 ? "*" : this.maxBound) + "]" : "") + ")");
        return "channel:" + name + " " + t;
    }
}
