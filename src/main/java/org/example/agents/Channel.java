package org.example.agents;

import org.omg.sysml.lang.sysml.Type;

import java.util.List;

public class Channel {
    public final String name;

    public Channel(String name) {
        this.name = name;

    }

    @Override
    public String toString() {
        return "channel:" +name ;
    }
}
