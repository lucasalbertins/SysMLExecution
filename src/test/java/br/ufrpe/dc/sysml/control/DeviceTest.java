package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.JoinNode;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;

import br.ufrpe.dc.sysml.SysMLV2Spec;

public class DeviceTest {
	
    private static SysMLV2Spec sysmlSpec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() {
        sysmlSpec = new SysMLV2Spec();
        sysmlSpec.parseFile("behavior/DeviceBattery.sysml");
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "The root namespace must not be null.");
    }
    
	private void printElementStructure(Element element, int indent) {
		String prefix = " ".repeat(indent);
		String className = element.getClass().getSimpleName();
		String name = element.getDeclaredName() != null ? element.getDeclaredName() : "<no-name>";
		System.out.printf("%s%s - %s%n", prefix, className, name);

		// Namespace recursion
		if (element instanceof Namespace ns) {
			for (Element child : ns.getOwnedMember()) {
				printElementStructure(child, indent + 1);
			}
		}
	}

    @Test
    void testPrintFullModelStructure() {
        assertNotNull(rootNamespace, "The root namespace must not be null.");
        System.out.println("=== FULL MODEL STRUCTURE ===");
        printElementStructure(rootNamespace, 0);
    }
}
