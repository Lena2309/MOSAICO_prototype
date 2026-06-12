package eu.mosaico_project.shadow_sysml.impl;


import eu.mosaico_project.shadow_sysml.Type;

public class DataTypeImpl implements Type {

    final String name ;

    public DataTypeImpl(org.omg.sysml.lang.sysml.DataType d) {
        this.name = d.getDeclaredName();
    }

    @Override
    public String toString() {
        return "DataType " + name ;
    }
}
