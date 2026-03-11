package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.ControlNode;
import org.omg.sysml.lang.sysml.DecisionNode;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.ForkNode;
import org.omg.sysml.lang.sysml.JoinNode;
import org.omg.sysml.lang.sysml.MergeNode;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.nodes.ControlNodeAdapter;
import br.ufrpe.dc.sysml.SysMLV2Spec;

public class ControlNodeAdapterTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() {
        spec = new SysMLV2Spec();
        spec.parseFile("control/ControlNodeTest.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "Namespace raiz não deve ser nulo");
    }

    // Utils
    private void collectControlNodes(Element elt, List<ControlNode> out) {
        if (elt == null) return;

        if (elt instanceof ControlNode cn) {
            out.add(cn);
        }

        if (elt instanceof Namespace ns) {
            for (Element member : ns.getOwnedMember()) {
                collectControlNodes(member, out);
            }
        }
    }
    
    private String resolveNodeType(ControlNodeAdapter adapter) {
	    if (adapter.isDecisionNode()) return "DecisionNode";
	    if (adapter.isForkNode()) return "ForkNode";
	    if (adapter.isJoinNode()) return "JoinNode";
	    if (adapter.isMergeNode()) return "MergeNode";
	    return "Unknown";
	}

    // Tests
    @Test
    void testControlNodeAdapterClassification() {
        List<ControlNode> nodes = new ArrayList<>();
        collectControlNodes(rootNamespace, nodes);

        assertFalse(nodes.isEmpty(),
                "Nenhum ControlNode encontrado no modelo");

        for (ControlNode node : nodes) {

            ControlNodeAdapter adapter = new ControlNodeAdapter(node);

            // Nome
            assertNotNull(adapter.getDeclaredName(),
                    "ControlNodeAdapter deve possuir nome declarado");

            String actualType = resolveNodeType(adapter);
            assertNotEquals("Unknown", actualType,
                    "ControlNode '" + adapter.getDeclaredName()
                    + "' não foi classificado");

            // Tipo
            String expectedType;
            if (node instanceof DecisionNode) {
                expectedType = "DecisionNode";
            } else if (node instanceof ForkNode) {
                expectedType = "ForkNode";
            } else if (node instanceof JoinNode) {
                expectedType = "JoinNode";
            } else if (node instanceof MergeNode) {
                expectedType = "MergeNode";
            } else {
                fail("Tipo de ControlNode desconhecido: "
                     + node.getClass().getSimpleName());
                return;
            }

            assertEquals(expectedType, actualType,
                    "Classificação incorreta para ControlNode '"
                    + adapter.getDeclaredName() + "'");

            // Print
            System.out.println("=== Node: " + node.getName() + " ===");
            System.out.println("Node Type: " + resolveNodeType(adapter) + ";\n");
        }
    }
}