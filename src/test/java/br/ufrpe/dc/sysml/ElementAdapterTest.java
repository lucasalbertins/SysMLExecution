package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.utils.ElementAdapter;
import br.ufrpe.dc.sysml.SysMLV2Spec;

class ElementAdapterTest {

    private static SysMLV2Spec sysmlSpec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() {
        sysmlSpec = new SysMLV2Spec();
        
        sysmlSpec.parseFile("control/DecisionExample.sysml");
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "Namespace raiz não deve ser nulo");
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
        assertFalse(elements.isEmpty(), "Nenhum elemento encontrado no modelo");

        System.out.println("=== TESTE DE ELEMENT IDs ===");

        for (Element elem : elements) {
            // cria adaptador
            ElementAdapter adapter = new ElementAdapter(elem);

            String directID = elem.getElementId();
            String adapterID = adapter.getID();

            String name = elem.getDeclaredName() != null ? elem.getDeclaredName() : "<no-name>";
            System.out.printf("Elemento: %-20s | Direto: %-40s | Adapter: %-40s%n",
                    name, directID, adapterID);

            // Verifica consistência
            if (directID != null) {
                assertEquals(directID, adapterID,
                        "IDs divergentes para o elemento: " + name);
            } else {
                assertEquals("<no-id>", adapterID,
                        "Esperava <no-id> para elemento sem ID: " + name);
            }
        }
    }
}