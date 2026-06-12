package eu.mosaico_project.shadow_sysml.impl;

/** Proxy is used for an element that is not available at parsing time, because of a forward reference. */
public class ProxyFeatureImpl extends ElementImpl implements eu.mosaico_project.shadow_sysml.Feature {
    public ProxyFeatureImpl(org.omg.sysml.lang.sysml.Element e) {
        super(e);
    }

    @Override
    public String toString() {
        return "PROXY";
    }
}
