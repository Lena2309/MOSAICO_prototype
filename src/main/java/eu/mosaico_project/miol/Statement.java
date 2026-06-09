package eu.mosaico_project.miol;

public interface Statement {
    void execute(ChannelState s, AttributeState memory);
}
