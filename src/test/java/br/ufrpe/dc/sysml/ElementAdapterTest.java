package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.utils.ElementAdapter;

public class ElementAdapterTest {

    private static SysMLV2Spec sysmlSpec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() {
        sysmlSpec = new SysMLV2Spec();
        
        sysmlSpec.parseFile("control/DecisionExample.sysml");
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "The root namespace must not be null.");
    }

    private void collectAllElements(Element element, List<Element> out) {
        if (element == null) return;
        out.add(element);
        if (element instanceof Namespace ns) {
            for (Element child : ns.getOwnedMember()) {
                collectAllElements(child, out);
            }
        }
    }

    @Test
    void testElementAdapterIDs() {
        List<Element> elements = new ArrayList<>();
        collectAllElements(rootNamespace, elements);
        assertFalse(elements.isEmpty(), "No element found in the model.");

        System.out.println("=== ELEMENT ID TEST ===");
        for (Element elem : elements) {
            ElementAdapter adapter = new ElementAdapter(elem);

            String directID = elem.getElementId();
            String adapterID = adapter.getID();

            String name = elem.getDeclaredName() != null ? elem.getDeclaredName() : "<no-name>";
            System.out.printf("Element: %-20s | Direct: %-40s | Adapter: %-40s%n",
                    name, directID, adapterID);

            // Checks consistency.
            if (directID != null) {
                assertEquals(directID, adapterID,
                        "Divergent IDs for the element: " + name);
            } else {
                assertEquals("<no-id>", adapterID,
                        "Expected <no-id> for the element without an ID " + name);
            }
        }
    }
}
