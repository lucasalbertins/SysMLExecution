package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import adapters.sv2pi.behavior.actions.nodes.FlowUsageAdapter;
import interfaces.behavior.actions.nodes.IFlowEnd;
import parser.SysMLV2Spec;

public class FlowAdapterMergeExampleTest {

    @Test
    void testAllFlowsInMergeExample() throws FileNotFoundException, IOException {
        SysMLV2Spec spec = new SysMLV2Spec();
        spec.parseFile("control/MergeExample.sysml");

        Namespace root = (Namespace) spec.getRootNamespace();
        assertNotNull(root, "Namespace must not be null");

        List<FlowUsage> flows = new LinkedList<>();
        collectAllFlowUsages(root, flows);
        assertFalse(flows.isEmpty(), "Expected to find FlowUsage(s) in MergeExample");

        // Expected mappings (source->target)
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
            System.out.println("---------------------------------------------------");

            // MergeExample: no payload, no name
            assertNull(adapter.getPayload(), "MergeExample flow must not have a payload");
            assertEquals(adapter.getName(), "<no-name>", "MergeExample flow must not have a name");

            // Source and target must be present
            IFlowEnd s = adapter.getSource();
            IFlowEnd t = adapter.getTarget();

            String sourceName = extractFlowEndName(s);
            String targetName = extractFlowEndName(t);
            String mapping = sourceName + "->" + targetName;
            
            assertTrue(expected.contains(mapping),
                "Unexpected mapping found: " + mapping + " (remaining expected: " + expected + ")");
            expected.remove(mapping);
        }

        // all expected should be matched
        assertTrue(expected.isEmpty(), "A expected mapping was not found: " + expected);
    }
    
    private String extractFlowEndName(IFlowEnd fe) {
        if (fe == null) return "null";
        
        String nodeName = "";
        String varName = "";
        
        if (fe.getReferencedFeature() != null && fe.getReferencedFeature().getName() != null) {
            nodeName = fe.getReferencedFeature().getName();
        }
        
        if (fe.getReferenceUsage() != null && fe.getReferenceUsage().getName() != null) {
            varName = fe.getReferenceUsage().getName();
            if (varName.contains("<no-name>")) {
                varName = "";
            }
        }
        
        if (!nodeName.isEmpty() && !varName.isEmpty()) {
            return nodeName + "." + varName;
        } else if (!nodeName.isEmpty()) {
            return nodeName; // Fallback
        } else {
            return fe.getName(); // Fallback
        }
    }
    
    // Helper
    private void collectAllFlowUsages(Element elt, List<FlowUsage> out) {
        if (elt == null) return;
        
        if (elt instanceof FlowUsage fu) {
            out.add(fu);
        }
        
        if (elt instanceof Namespace ns) {
            for (int i = 0; i < ns.getOwnedMember().size(); i++) {
                collectAllFlowUsages(ns.getOwnedMember().get(i), out);
            }
        } else {
            try {
                if (elt instanceof Feature f) {
                    for (int i = 0; i < f.getOwnedFeature().size(); i++) {
                        collectAllFlowUsages(f.getOwnedFeature().get(i), out);
                    }
                }
            } catch (Exception ex) {
            }
        }
    }
}