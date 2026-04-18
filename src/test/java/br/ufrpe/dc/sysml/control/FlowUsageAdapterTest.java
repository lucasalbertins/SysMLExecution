package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.FlowUsage;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.nodes.FlowUsageAdapter;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.utils.INamedElement;

import br.ufrpe.dc.sysml.SysMLV2Spec;

public class FlowUsageAdapterTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() throws IOException {
        spec = new SysMLV2Spec();
        spec.parseFile("control/ForkJoinExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "The root namespace must not be null.");
    }

    // Utils
    private void collectAllFlowUsages(Element elt, List<FlowUsage> out) {
        if (elt == null) return;

        if (elt instanceof FlowUsage fu) {
            out.add(fu);
        }
        if (elt instanceof Namespace ns) {
            for (Element member : ns.getOwnedMember()) {
                collectAllFlowUsages(member, out);
            }
        }
    }

    private String toPath(IFlowEnd end) {
        StringBuilder sb = new StringBuilder();

        if (end.getReferencedFeature() != null) {
            sb.append(end.getReferencedFeature().getDeclaredName()).append(".");
        }
        for (INamedElement ine : end.getChainingFeatures()) {
            sb.append(ine.getName()).append(".");
        }
        sb.append(end.getReferenceUsage().getName());
        return sb.toString();
    }

    // Tests
    @Test
    void testFlowUsageAdaptersStructureAndConsistency() {
        List<FlowUsage> flows = new ArrayList<>();
        collectAllFlowUsages(rootNamespace, flows);

        assertFalse(flows.isEmpty(),
                "No FlowUsage found in the model.");

        for (FlowUsage flow : flows) {

            FlowUsageAdapter adapter = new FlowUsageAdapter(flow);
            // Name
            assertNotNull(adapter.getDeclaredName(),
                    "FlowUsageAdapter must have a declared name.");
            
            // Payload
            if (adapter.getPayload() != null) {
                assertNotNull(adapter.getPayload().getName(),
                        "Payload must have a name.");
            }

            // Source
            IFlowEnd source = adapter.getSource();
            assertNotNull(source,
                    "Flow '" + adapter.getDeclaredName() + "' must have a source.");
            assertNotNull(source.getReferenceUsage(),
                    "Source must have a ReferenceUsage.");
            assertNotNull(source.getChainingFeatures(),
                    "Source must have an array of chaining features (even if empty).");
            if (source.getReferencedFeature() != null) {
                assertNotNull(source.getReferencedFeature().getDeclaredName(),
                        "The source's ReferencedFeature must have a name.");
            }
            assertDoesNotThrow(() -> toPath(source),
                    "Failed to build the source path.");

            // Target
            IFlowEnd target = adapter.getTarget();
            assertNotNull(target,
                    "Flow '" + adapter.getDeclaredName() + "' must have a target.");
            assertNotNull(target.getReferenceUsage(),
                    "Target must have a ReferenceUsage.");
            assertNotNull(target.getChainingFeatures(),
                    "Target must have an array of chaining features (even if empty).");
            if (target.getReferencedFeature() != null) {
                assertNotNull(target.getReferencedFeature().getDeclaredName(),
                        "The targe's ReferencedFeature must have a name.");
            }
            assertDoesNotThrow(() -> toPath(target),
                    "Failed to build the target path.");
            
            // Print
            System.out.println("\n=== Testing FlowUsageAdapter for: " + adapter.getDeclaredName() + " ===");
            System.out.println("Payload: " + (adapter.getPayload() != null ? adapter.getPayload().getName() : "<no-payload>"));
            if (adapter.getSource() != null) {
                System.out.println("Source: " + toPath(source));
            } else {
                System.out.println("Source: <no-source>");
            }

            if (adapter.getTarget() != null) {
                System.out.println("Target: " + toPath(target));
            } else {
                System.out.println("Target: <no-target>");
            }
            
            // flowName of payloadType from sourceEnd to targetEnd
            StringBuilder sb = new StringBuilder();
        	sb.append("\nflow ");
        	if (adapter.getName() != "<no-name>") {
        		sb.append(adapter.getName());
        	}
        	if (adapter.getPayload() != null) {
        		sb.append(" of ").append(adapter.getPayload().getName());
        	}
        	sb.append("\nfrom ").append(toPath(source));
        	sb.append("\nto ").append(toPath(target));
        	System.out.println(sb.toString());
        }
    }
}
