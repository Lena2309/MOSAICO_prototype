package eu.mosaico_project.dto;

public class NoOp implements Statement {
    @Override
    public void execute(ChannelState s, AttributeState memory) {
        System.out.println("noop");
    }
}
