package eu.mosaico_project.shadow_sysml.impl;

public class PackageImpl extends NamespaceImpl implements eu.mosaico_project.shadow_sysml.Package {

    public PackageImpl(org.omg.sysml.lang.sysml.Package p) {
        super(p);
    }

    @Override
    public String toString(){
        return "PACKAGE " + this.declaredName ;
    }

}
