package org.example.dto.task.output;

import java.util.Optional;

public class Channel {
    private final String name;
    String type;

    public Channel(String name, Optional<String> type) {
        this.name = name;
        this.type = type.orElse(null);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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
        final String t = (this.type == null ? "(no type)" : "(" + this.type + ")");
        return "channel:" + name + " " + t;
    }
}
