package org.example.parser.util;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.SysMLPackage;

public interface UtilAttributeMapper {
    static String getSafeName(Element e) {
        if (e == null) return "null";

        if ((e.eIsProxy()) && (e.eResource() != null)) {
            e = (Element) EcoreUtil.resolve(e, e.eResource().getResourceSet());
        }

        String name = e.getDeclaredName();
        if (name == null) {
            try {
                var nodes = NodeModelUtils.findNodesForFeature(e, SysMLPackage.Literals.ELEMENT__DECLARED_NAME);
                if (!nodes.isEmpty()) return NodeModelUtils.getTokenText(nodes.getFirst());
            } catch (Exception ignored) {
            }
            name = "<unnamed>";
        }
        return name;
    }
}
