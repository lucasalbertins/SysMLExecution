package br.ufrpe.dc.sysml.control;


import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FlowUsage;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.nodes.FlowUsageAdapter;
import br.ufrpe.dc.sysml.SysMLV2Spec;

public class FlowAdapterFlowUsageTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() throws IOException {
        spec = new SysMLV2Spec();
        spec.parseFile("control/FlowUsageExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "Namespace raiz nÃ£o deve ser nulo");
    }

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

    @Test
    void testFlowAdapters() {
        List<FlowUsage> flows = new ArrayList<>();
        collectAllFlowUsages(rootNamespace, flows);

        assertFalse(flows.isEmpty(), "Nenhum FlowUsage encontrado no modelo");

        // percorre cada flowUsage encontrado
        for (FlowUsage flow : flows) {
            System.out.println("\n=== TESTANDO FLOW ADAPTER PARA: " + flow.getDeclaredName() + " ===");

            Namespace container = (Namespace) flow.getOwner();
            if (container == null) container = rootNamespace;

            FlowUsageAdapter adapter = new FlowUsageAdapter(flow);

            System.out.println("getDeclaredName: " + (flow.getDeclaredName() != null ? flow.getDeclaredName() : "<sem-nome>"));
            System.out.println("getName: " + adapter.getName());
            System.out.println("Payload: " + (adapter.getPayload() != null ? adapter.getPayload() : "<no-payload>"));
            System.out.println("Source: " + (adapter.getSource() != null ? adapter.getSource() : "<no-source>"));
            System.out.println("Target: " + (adapter.getTarget() != null ? adapter.getTarget() : "<no-target>"));

            System.out.println("=== Fim do teste para " + adapter.getName() + " ===");
        }
    }
}