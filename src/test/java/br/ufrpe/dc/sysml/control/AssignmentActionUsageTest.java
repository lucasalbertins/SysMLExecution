package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.AssignmentActionUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.nodes.AssignmentActionUsageAdapter;
import br.ufrpe.dc.sysml.SysMLV2Spec;

public class AssignmentActionUsageTest {
	
	private static SysMLV2Spec spec;
	private static Namespace rootNamespace;
	
	@BeforeAll
    static void init() throws IOException {
        spec = new SysMLV2Spec();
        spec.parseFile("control/NewDecisionNodeExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "The root namespace must not be null.");
    }
	
	private void collectAllAssignmentActions(Element elt, List<AssignmentActionUsage> out) {
        if (elt == null) return;

        if (elt instanceof AssignmentActionUsage a) {
            out.add(a);
        }
        if (elt instanceof Namespace ns) {
            for (Element member : ns.getOwnedMember()) {
            	collectAllAssignmentActions(member, out);
            }
        }
    }
	
	public void printElementStructure(Element element, int indent) {
    	String prefix = " ".repeat(indent);
    	String className = element.getClass().getSimpleName();
    	String name = element.getDeclaredName() != null ? element.getDeclaredName() : "<no-name>";
    	System.out.printf("%s%s - %s%n", prefix, className, name);
    	//System.out.println(element);
    	
    	if (element instanceof Namespace ns) {
    		for (Element child : ns.getOwnedMember()) {
    			printElementStructure(child, indent + 1);
    		}
    	}
    }
	
	@Test
    void testFlowAdapters() {
		printElementStructure(rootNamespace.getOwnedElement().getFirst(), 3);
		System.out.println();
        List<AssignmentActionUsage> aaus = new ArrayList<>();
        collectAllAssignmentActions(rootNamespace, aaus);

        assertFalse(aaus.isEmpty(), "No AssignmentActionUsage found in the model.");

        for (AssignmentActionUsage aau : aaus) {
            Namespace container = (Namespace) aau.getOwner();
            if (container == null) container = rootNamespace;

            AssignmentActionUsageAdapter adapter = new AssignmentActionUsageAdapter(aau);
            System.out.println(adapter.getDeclaredName());

        }
    }
}
