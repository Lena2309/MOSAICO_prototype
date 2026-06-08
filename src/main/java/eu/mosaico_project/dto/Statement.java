package eu.mosaico_project.dto;

public interface Statement {
    void execute(ChannelState s, AttributeState memory);
}
