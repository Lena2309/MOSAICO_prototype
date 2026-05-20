package org.example.dto;

import org.example.dto.task.output.TaskOutput;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class ThreadSafeChannelState extends CopyOnWriteArrayList<TaskOutput> implements ChannelState {
    public ThreadSafeChannelState() {
        super();
    }

    public ThreadSafeChannelState(Collection<? extends TaskOutput> c) {
        super(c);
    }
}