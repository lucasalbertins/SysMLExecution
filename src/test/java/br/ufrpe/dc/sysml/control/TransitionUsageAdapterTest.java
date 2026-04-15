package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.TransitionUsage;

import adapters.behavior.actions.ActionUsageAdapterRegistry;
import adapters.behavior.actions.nodes.TransitionUsageAdapter;
import br.ufrpe.dc.sysml.SysMLV2Spec;

public class TransitionUsageAdapterTest extends SysMLInteractiveTest {
	
	private static SysMLV2Spec spec;
    private static Namespace rootNamespace;
    public static ActionUsageAdapterRegistry registry;
    
    @BeforeAll
    static void init() {
        spec = new SysMLV2Spec();
        spec.parseFile("control/NewDecisionNodeExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        registry = new ActionUsageAdapterRegistry(rootNamespace);
        assertNotNull(rootNamespace, "Namespace raiz não deve ser nulo");
    }

    // Utils
    private void collectTransitionUsages(Element elt, List<TransitionUsage> out) {
        if (elt == null) return;
        if (elt instanceof TransitionUsage tu) {
           out.add(tu);
        }
        if (elt instanceof Namespace ns) {
            for (Element member : ns.getOwnedMember()) {
            	collectTransitionUsages(member, out);
            }
        }
    }
    
    @Test
	void test() {
		List<TransitionUsage> transitionUsages = new ArrayList<>();
		collectTransitionUsages(rootNamespace, transitionUsages);
		assertFalse(transitionUsages.isEmpty(), "Nenhum TransitionUsage encontrado no modelo");

		for (TransitionUsage transitionUsage : transitionUsages) {
			TransitionUsageAdapter adapter = new TransitionUsageAdapter(transitionUsage);
			
			Element chargeBattery = registry.getByDeclaredName("chargeBattery").getFirst().getElement();
			boolean isTransitionValid = adapter.evaluateGuard(chargeBattery);

			System.out.println("\nTesting TransitionUsage "
							   + "\n   Source: " + adapter.getSource().getDeclaredName()
							   + "\n   Target: " + adapter.getTarget().getDeclaredName()
							   + "\n    Guard: " + isTransitionValid);
		}
	}
}
