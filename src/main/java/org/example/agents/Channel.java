package org.example.agents;

import java.util.Optional;

public class Channel {
    public final String name;
    String type;

    public Channel(String name, Optional<String> t) {
        this.name = name;
        if (t.isPresent())
            type = t.get();
        else
            type = null ;
    }

    @Override
    public String toString() {
        final String t = (this.type == null ? "(no type)" : "(" + this.type + ")");
        return "channel:" + name + " " + t ;
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
}
