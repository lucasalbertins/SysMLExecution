package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FlowUsage;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.nodes.FlowUsageAdapter;
import br.ufrpe.dc.sysml.SysMLV2Spec;
import interfaces.behavior.actions.nodes.IFlowEnd;

public class FlowAdapterMergeExampleTest {

    @Test
    void testAllFlowsInMergeExample() throws FileNotFoundException, IOException {
        SysMLV2Spec spec = new SysMLV2Spec();
        spec.parseFile("control/MergeExample.sysml");

        Namespace root = (Namespace) spec.getRootNamespace();
        assertNotNull(root, "Namespace raiz não deve ser nulo");

        List<FlowUsage> flows = new LinkedList<>();
        collectAllFlowUsages(root, flows);
        assertFalse(flows.isEmpty(), "Esperava encontrar FlowUsage(s) no MergeExample");

        // expected mappings (source->target)
        Set<String> expected = new HashSet<>();
        expected.add("trigger.scene->focus.scene");
        expected.add("focus.image->shoot.image");
        expected.add("shoot.picture->display.picture");

        System.out.println("=== MergeExample: Found " + flows.size() + " FlowUsage(s) ===");

        for (FlowUsage fu : flows) {
            FlowUsageAdapter adapter = new FlowUsageAdapter(fu);

            String declared = fu.getDeclaredName() != null ? fu.getDeclaredName() : "<no-name>";
            System.out.println("FlowUsage raw declaredName: " + declared);
            System.out.println("Adapter.getName(): " + adapter.getName());
            System.out.println("Adapter.getPayload(): " + adapter.getPayload());
            System.out.println("Adapter.getSource(): " + adapter.getSource());
            System.out.println("Adapter.getTarget(): " + adapter.getTarget());
            System.out.println("Adapter.toString(): " + adapter.toString());
            System.out.println("---------------------------------------------------");

            // MergeExample: no payload, no name
            assertNull(adapter.getPayload(), "MergeExample flow não deve ter payload");
            assertNull(adapter.getName(), "MergeExample flow não deve ter declaredName");

            // source and target must be present
            IFlowEnd s = adapter.getSource();
            IFlowEnd t = adapter.getTarget();
            assertNotNull(s, "Fonte do flow não deve ser nula");
            assertNotNull(t, "Alvo do flow não deve ser nulo");

            String mapping = s + "->" + t;
            assertTrue(expected.contains(mapping),
                "Mapping inesperado encontrado: " + mapping + " (esperados: " + expected + ")");
            expected.remove(mapping);
        }

        // all expected should be matched
        assertTrue(expected.isEmpty(), "Algum mapping esperado não foi encontrado: " + expected);
    }
    
    // Interessante criar uma classe Helper para fazer isso
    private void collectAllFlowUsages(Element elt, List<FlowUsage> out) {
        if (elt == null) return;
        if (elt instanceof FlowUsage fu) {
            out.add(fu);
        }
        if (elt instanceof Namespace ns) {
            for (Element child : ns.getOwnedMember()) {
                collectAllFlowUsages(child, out);
            }
        } else {
            try {
                if (elt instanceof Feature f) {
                    for (Feature child : f.getOwnedFeature()) {
                        collectAllFlowUsages(child, out);
                    }
                }
            } catch (Exception ex) {
                // ignore
            }
        }
    }
}