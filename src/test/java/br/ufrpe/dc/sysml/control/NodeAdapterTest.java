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
import interfaces.behavior.actions.ISuccession;

import br.ufrpe.dc.sysml.SysMLV2Spec;

public class NodeAdapterTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() {
        spec = new SysMLV2Spec();
        spec.parseFile("control/DecisionExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "The root namespace must not be null.");
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

    @Test
    void testNodeAdapterIncomingAndOutgoingSuccessions() {
        List<ActionUsage> actions = new ArrayList<>();
        collectAllActions(rootNamespace, actions);

        assertFalse(actions.isEmpty(),
                "No ActionUsage found in the model.");

        for (ActionUsage action : actions) {

            NodeAdapter adapter = new NodeAdapter(action);

            List<ISuccession> incomings = adapter.getIncomings();
            List<ISuccession> outgoings = adapter.getOutgoings();

            assertNotNull(incomings,
                    "getIncomings() must not return null.");
            assertNotNull(outgoings,
                    "getOutgoings() must not return null.");

            // Incomings
            for (ISuccession inc : incomings) {
                assertNotNull(inc,
                        "Succession incoming must not be null.");

                assertNotNull(inc.getSource(),
                        "Incoming must have a source.");

                assertNotNull(inc.getTarget(),
                        "Incoming must have a target.");
                assertEquals(adapter.getID(),
                        inc.getTarget().getID(),
                        "Inside incoming, the current node should be the target.");
            }

            // Outgoings
            for (ISuccession out : outgoings) {
                assertNotNull(out,
                        "Succession outgoing must not be null.");

                assertNotNull(out.getSource(),
                        "Outgoing must have a source.");

                assertNotNull(out.getTarget(),
                        "Outgoing must have a target.");

                assertEquals(adapter.getID(),
                        out.getSource().getID(),
                        "Inside outgoing, the current node should be the source.");
            }

            // Intersections
            Set<ISuccession> intersection = new HashSet<>();
            for (ISuccession inc : incomings) {
                for (ISuccession out : outgoings) {
                    if (inc.equals(out)) {
                        intersection.add(inc);
                    }
                }
            }
            // Different way of testing intersections.
            Set<String> incomingIds = new HashSet<>();
            for (ISuccession inc : incomings) {
                incomingIds.add(inc.getID());
            }

            for (ISuccession out : outgoings) {
                assertFalse(
                    incomingIds.contains(out.getID()),
                    "The same succession cannot be both incoming and outgoing of the same node."
                );
            }
            assertTrue(intersection.isEmpty(),
                    "The same succession cannot be both incoming and outgoing of the same node.");
            
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
