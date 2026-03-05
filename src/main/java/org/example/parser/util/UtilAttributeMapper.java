package org.example.parser.util;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.SysMLPackage;

/**
 * Utility interface providing helper methods for extracting and mapping
 * attributes from SysML model elements.
 */
public interface UtilAttributeMapper {

    /**
     * Attempts to retrieve a human-readable name for a given SysML element.
     * <p>
     * This method is "safe" because it handles:
     * <ul>
     * <li>Null elements</li>
     * <li>EMF Proxy resolution (loading elements from external resources)</li>
     * <li>Xtext Node Model recovery (fetching names directly from the source text
     * if the semantic model hasn't populated {@code declaredName} yet)</li>
     * </ul>
     *
     * @param e The SysML {@link Element} to inspect.
     * @return The declared name, the raw token text from the source,
     * or {@code "<unnamed>"} if no name can be resolved.
     */
    static String getSafeName(Element e) {
        if (e == null) return "null";

        // Handle EMF Proxies: If the element is a proxy, attempt to resolve it
        // within the current ResourceSet to access its actual data.
        if ((e.eIsProxy()) && (e.eResource() != null)) {
            e = (Element) EcoreUtil.resolve(e, e.eResource().getResourceSet());
        }

        String name = e.getDeclaredName();

        // Fallback logic for unnamed elements or uninitialized semantic features
        if (name == null) {
            try {
                // Attempt to recover the name by looking at the underlying Xtext node model (the grammar tokens)
                var nodes = NodeModelUtils.findNodesForFeature(e, SysMLPackage.Literals.ELEMENT__DECLARED_NAME);
                if (!nodes.isEmpty()) {
                    return NodeModelUtils.getTokenText(nodes.getFirst());
                }
            } catch (Exception ignored) {
                // Fail silently and proceed to the default placeholder
            }
            name = "<unnamed>";
        }
        return name;
    }
}