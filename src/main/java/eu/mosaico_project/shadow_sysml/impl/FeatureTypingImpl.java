package eu.mosaico_project.shadow_sysml.impl;

public class FeatureTypingImpl extends ElementImpl {
    final String type;



    public FeatureTypingImpl(org.omg.sysml.lang.sysml.FeatureTyping e) {
        super(e);
        this.type = e.getType().getDeclaredName();
    }

    @Override
    public String toString() {
        return "FEAT_TYPING: " + type ;
    }
}
