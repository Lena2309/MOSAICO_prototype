package eu.mosaico_project.shadow_sysml.impl;


import eu.mosaico_project.shadow_sysml.Element;

/** Proxy is used for an element that is not available at parsing time, because of a forward reference. */
public class ProxyElementImpl extends ElementImpl implements Element {
    public ProxyElementImpl(org.omg.sysml.lang.sysml.Element e) {
        super(e);
    }

    @Override
    public String toString() {
        return "PROXY";
    }
}
