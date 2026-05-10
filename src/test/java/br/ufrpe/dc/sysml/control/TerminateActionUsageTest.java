package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.TerminateActionUsage;

import adapters.behavior.actions.TerminateActionUsageAdapter;
import br.ufrpe.dc.sysml.SysMLV2Spec;

public class TerminateActionUsageTest {
	
	private static SysMLV2Spec spec;
	private static Namespace rootNamespace;
	
	
	@BeforeAll
    static void init() throws IOException {
        spec = new SysMLV2Spec();
        spec.parseFile("control/TerminateArgumentExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "The root namespace must not be null.");
    }
	
	private void collectAllTerminateActions(Element elt, List<TerminateActionUsage> out) {
        if (elt == null) return;

        if (elt instanceof TerminateActionUsage fu) {
            out.add(fu);
        }
        if (elt instanceof Namespace ns) {
            for (Element member : ns.getOwnedMember()) {
            	collectAllTerminateActions(member, out);
            }
        }
    }
	
	@Test
    void testFlowAdapters() {
        List<TerminateActionUsage> taus = new ArrayList<>();
        collectAllTerminateActions(rootNamespace, taus);

        assertFalse(taus.isEmpty(), "No TerminateActionUsage found in the model.");

        // Iterates through each FlowUsage found.
        for (TerminateActionUsage tau : taus) {
            System.out.println("\n======");

            Namespace container = (Namespace) tau.getOwner();
            if (container == null) container = rootNamespace;

            TerminateActionUsageAdapter adapter = new TerminateActionUsageAdapter(tau);
            System.out.println(adapter.getDeclaredName());
            System.out.println("FRE" + adapter.getfeatureReferenceExpression()); // não está captando
            System.out.println("FCE" + adapter.getfeatureChainExpression());
        }
    }
	
	

}
