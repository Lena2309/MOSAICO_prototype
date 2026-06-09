package eu.mosaico_project.miol;

public class NoOp implements Statement {
    @Override
    public void execute(ChannelState s, AttributeState memory) {
        System.out.println("noop");
    }
}
