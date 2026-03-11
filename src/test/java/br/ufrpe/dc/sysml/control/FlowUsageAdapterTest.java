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
import br.ufrpe.dc.sysml.SysMLV2Spec;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.utils.INamedElement;

public class FlowUsageAdapterTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() throws IOException {
        spec = new SysMLV2Spec();
        spec.parseFile("control/ForkJoinExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "Namespace raiz nÃ£o deve ser nulo");
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
                "Nenhum FlowUsage encontrado no modelo");

        for (FlowUsage flow : flows) {

            FlowUsageAdapter adapter = new FlowUsageAdapter(flow);

            // Nome
            assertNotNull(adapter.getDeclaredName(),
                    "FlowUsageAdapter deve possuir nome declarado");

            // Payload
            if (adapter.getPayload() != null) {
                assertNotNull(adapter.getPayload().getName(),
                        "Payload deve possuir nome");
            }

            // Source
            IFlowEnd source = adapter.getSource();
            assertNotNull(source,
                    "Flow '" + adapter.getDeclaredName() + "' deve possuir source");

            assertNotNull(source.getReferenceUsage(),
                    "Source deve possuir ReferenceUsage");

            assertNotNull(source.getChainingFeatures(),
                    "Source deve possuir array de chaining features (mesmo vazio)");

            if (source.getReferencedFeature() != null) {
                assertNotNull(source.getReferencedFeature().getDeclaredName(),
                        "ReferencedFeature do source deve possuir nome");
            }

            assertDoesNotThrow(() -> toPath(source),
                    "Falha ao construir path do source");

            // Target
            IFlowEnd target = adapter.getTarget();
            assertNotNull(target,
                    "Flow '" + adapter.getDeclaredName() + "' deve possuir target");

            assertNotNull(target.getReferenceUsage(),
                    "Target deve possuir ReferenceUsage");

            assertNotNull(target.getChainingFeatures(),
                    "Target deve possuir array de chaining features (mesmo vazio)");

            if (target.getReferencedFeature() != null) {
                assertNotNull(target.getReferencedFeature().getDeclaredName(),
                        "ReferencedFeature do target deve possuir nome");
            }

            assertDoesNotThrow(() -> toPath(target),
                    "Falha ao construir path do target");
            
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