package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.TransitionUsage;

import adapters.behavior.actions.nodes.NodeAdapter;
import br.ufrpe.dc.sysml.SysMLV2Spec;
import interfaces.behavior.actions.ISuccession;

class NodeAdapterTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() {
        spec = new SysMLV2Spec();
        spec.parseFile("control/DecisionExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "Namespace raiz não deve ser nulo");
    }

    // Utils
    private void collectAllActions(Element elt, List<ActionUsage> out) {
        if (elt == null) return;

        if (elt instanceof ActionUsage au && !(elt instanceof TransitionUsage)) {
            out.add(au);
        }

        if (elt instanceof Namespace ns) {
            for (Element member : ns.getOwnedMember()) {
                collectAllActions(member, out);
            }
        }
    }

    // Tests
    @Test
    void testNodeAdapterIncomingAndOutgoingSuccessions() {
        List<ActionUsage> actions = new ArrayList<>();
        collectAllActions(rootNamespace, actions);

        assertFalse(actions.isEmpty(),
                "Nenhuma ActionUsage encontrada no modelo");

        for (ActionUsage action : actions) {

            NodeAdapter adapter = new NodeAdapter(action);

            List<ISuccession> incomings = adapter.getIncomings();
            List<ISuccession> outgoings = adapter.getOutgoings();

            assertNotNull(incomings,
                    "getIncomings() não deve retornar null");
            assertNotNull(outgoings,
                    "getOutgoings() não deve retornar null");

            // Incomings
            for (ISuccession inc : incomings) {
                assertNotNull(inc,
                        "Succession incoming não deve ser nula");

                assertNotNull(inc.getSource(),
                        "Incoming deve ter source");

                assertNotNull(inc.getTarget(),
                        "Incoming deve ter target");
                // Traballhando com ID
                assertEquals(adapter.getID(),
                        inc.getTarget().getID(),
                        "Em incoming, o node atual deve ser o TARGET");
            }

            // Outgoings
            for (ISuccession out : outgoings) {
                assertNotNull(out,
                        "Succession outgoing não deve ser nula");

                assertNotNull(out.getSource(),
                        "Outgoing deve ter source");

                assertNotNull(out.getTarget(),
                        "Outgoing deve ter target");

                assertEquals(adapter.getID(),
                        out.getSource().getID(),
                        "Em outgoing, o node atual deve ser o SOURCE");
            }

            // Interseções
            Set<ISuccession> intersection = new HashSet<>();
            for (ISuccession inc : incomings) {
                for (ISuccession out : outgoings) {
                    if (inc.equals(out)) {
                        intersection.add(inc);
                    }
                }
            }
            // outra forma de testar interseções
            Set<String> incomingIds = new HashSet<>();
            for (ISuccession inc : incomings) {
                incomingIds.add(inc.getID());
            }

            for (ISuccession out : outgoings) {
                assertFalse(
                    incomingIds.contains(out.getID()),
                    "Uma mesma Succession não pode ser incoming e outgoing do mesmo node"
                );
            }

            assertTrue(intersection.isEmpty(),
                    "Uma mesma Succession não pode ser incoming e outgoing do mesmo node");
            
            // Print
            System.out.println("\n=== Testing NodeAdapter for: " + action.getDeclaredName() + " ===");      
            System.out.println("INCOMING SUCCESSIONS");
            for (ISuccession inc : adapter.getIncomings()) {
                String srcName = (inc.getSource() != null) ? inc.getSource().getDeclaredName() : "<null>";
                String tgtName = (inc.getTarget() != null) ? inc.getTarget().getDeclaredName() : "<null>";
                System.out.println("From: " + srcName + " \nTo: " + tgtName);
            }
            
            System.out.println("\nOUTGOING SUCCESSIONS");
            for (ISuccession out : adapter.getOutgoings()) {
                String srcName = (out.getSource() != null) ? out.getSource().getDeclaredName() : "<null>";
                String tgtName = (out.getTarget() != null) ? out.getTarget().getDeclaredName() : "<null>";
                System.out.println("From: " + srcName + "\nTo: " + tgtName + "\nGuard: <no-guard>");
            }
        }
    }
}