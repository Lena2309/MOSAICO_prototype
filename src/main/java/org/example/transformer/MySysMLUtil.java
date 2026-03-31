package org.example.transformer;

import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.omg.kerml.xtext.KerMLStandaloneSetup;
import org.omg.sysml.lang.sysml.SysMLPackage;
import org.omg.sysml.util.SysMLUtil;
import org.omg.sysml.xtext.SysMLStandaloneSetup;

import java.nio.file.Paths;

public class MySysMLUtil extends SysMLUtil {
    public MySysMLUtil(String libPrefix) {
        SysMLPackage.eINSTANCE.getName();
        KerMLStandaloneSetup.doSetup();
        SysMLStandaloneSetup.doSetup();

        String prefix = Paths.get(libPrefix).toAbsolutePath().toString() + "/";
        this.readAll(prefix + "Kernel Libraries", false, ".kerml");
        this.readAll(prefix + "Systems Library", false, ".sysml");
        this.readAll(prefix + "Domain Libraries", false, ".sysml");

        getResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
    }
}
