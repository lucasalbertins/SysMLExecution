package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;

import br.ufrpe.dc.sysml.SysMLV2Spec;

public class ExploreUUIDTest {

    private static SysMLV2Spec sysmlSpec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() {
        sysmlSpec = new SysMLV2Spec();
        sysmlSpec.parseFile("control/DecisionExample.sysml");
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "Namespace raiz não deve ser nulo");
    }

    // Explores elements recursively and print the UUID.
    private void exploreElements(Element element, int indent) {
        String prefix = "  ".repeat(indent);
        String className = element.getClass().getSimpleName();
        String name = element.getDeclaredName() != null ? element.getDeclaredName() : "<no-name>";
        String id = element.getElementId() != null ? element.getElementId().toString() : "<no-id>";

        System.out.printf("%s%s - %s [UUID: %s]%n", prefix, className, name, id);

        if (element instanceof Namespace ns) {
            for (Element child : ns.getOwnedMember()) {
                exploreElements(child, indent + 1);
            }
        }
    }

    @Test
    void testExploreAllElements() {
        assertNotNull(rootNamespace, "The root namespace must not be null.");
        System.out.println("=== EXPLORING ELEMENTS OF THE MODEL ===");
        exploreElements(rootNamespace, 0);
    }
}
