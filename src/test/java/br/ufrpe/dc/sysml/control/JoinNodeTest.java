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

public class JoinNodeTest {
	
    private static SysMLV2Spec sysmlSpec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() {
        sysmlSpec = new SysMLV2Spec();
        sysmlSpec.parseFile("control/ForkJoinExample.sysml");
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "The root namespace must not be null.");
    }

    // Iterates through the Namespace and prints the model structure with the classes of the corresponding elements.
    private void printElementStructure(Element element, int indent) {
        String prefix = "  ".repeat(indent);
        String className = element.getClass().getSimpleName();
        String name = element.getDeclaredName() != null ? element.getDeclaredName() : "<no-name>";
        System.out.printf("%s%s - %s%n", prefix, className, name);

        if (element instanceof Namespace ns) {
            for (Element child : ns.getOwnedMember()) {
                printElementStructure(child, indent + 1);
            }
        }

        if (element instanceof JoinNode jn) {
            if (!jn.getOwnedFeature().isEmpty()) {
                System.out.println("JoinNode Features:");
                for (Feature ffeat : jn.getOwnedFeature()) {
                    String fclass = ffeat.eClass().getName();
                    String fdecl = ffeat.getDeclaredName() != null ? ffeat.getDeclaredName()
                            : (ffeat.getName() != null ? ffeat.getName() : "<no-name>");
                    System.out.printf("%s    Owned Feature: %s - %s%n", prefix, fclass, fdecl);
                }
            } else {
                System.out.printf("JoinNode %s doesn't have features %n", jn.getDeclaredName());
            }
        }

        if (element instanceof SuccessionAsUsage su) {
            // Uses getSource() and getTarget() to showcase real connections.
            System.out.println(prefix + "SuccessionAsUsage:");
            if (!su.getSource().isEmpty()) {
                for (Element src : su.getSource()) {
                    String srcName = src.getDeclaredName() != null ? src.getDeclaredName() : "<no-name>";
                    System.out.printf("%s    Source -> %s (%s)%n", prefix, srcName, src.getClass().getSimpleName());
                }
            } else {
                System.out.printf("%s    No Source%n", prefix);
            }
            if (!su.getTarget().isEmpty()) {
                for (Element tgt : su.getTarget()) {
                    String tgtName = tgt.getDeclaredName() != null ? tgt.getDeclaredName() : "<no-name>";
                    System.out.printf("%s    Target -> %s (%s)%n", prefix, tgtName, tgt.getClass().getSimpleName());
                }
            } else {
                System.out.printf("%s    No Target%n", prefix);
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
