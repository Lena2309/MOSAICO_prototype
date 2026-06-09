package eu.mosaico_project.miol;

import eu.mosaico_project.miol.task.output.TaskOutput;

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