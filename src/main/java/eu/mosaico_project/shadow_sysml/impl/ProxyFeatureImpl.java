package eu.mosaico_project.shadow_sysml.impl;

import java.security.InvalidParameterException;

/** Proxy is used for an element that is not available at parsing time, because of a forward reference. */
public class ProxyFeatureImpl extends ElementImpl implements eu.mosaico_project.shadow_sysml.Feature {
    final String resource ;

    public ProxyFeatureImpl(org.omg.sysml.lang.sysml.Element e) {
        super(e);
        if (! e.eIsProxy())
            throw new InvalidParameterException("This element is not a proxy.");
        if (e.eResource() == null)
            this.resource = "(unbound "+ ((org.omg.sysml.lang.sysml.impl.ElementImpl) e).eProxyURI() + ")";
        else
            this.resource = e.eResource().toString() ;
    }

    @Override
    public String toString() {
        return "PROXY " + this.resource;
    }
}
