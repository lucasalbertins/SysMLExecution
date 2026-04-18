package br.ufrpe.dc.sysml;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.*;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;

// OUTDATED
public class SimpleSuccessionTest {
	   private static SysMLV2Spec sysmlSpec;
	    private static Namespace root;
	    private static List<SuccessionAsUsage> successions;

	    @BeforeAll
	    static void setUp() {
	        sysmlSpec = new SysMLV2Spec();
	        sysmlSpec.parseFileWithTransform("SimpleSuccession.sysml");
	        root = sysmlSpec.getRootNamespace();
	        assertNotNull(root, "Namespace raiz não pode ser nulo.");
	        successions = collectSuccessionAsUsages(root);
	        assertFalse(successions.isEmpty(), "Deveria haver pelo menos 1 SuccessionAsUsage.");
	    }

	    @Test
	    void testExplicitSuccessionOrder() {
	        System.out.println("Lista de SuccessionAsUsage encontradas");
	        for (SuccessionAsUsage su : successions) {
	            String src = su.getSource().stream()
	                .filter(e -> e instanceof ActionUsage)
	                .map(e -> ((ActionUsage)e).getDeclaredName())
	                .findFirst().orElse("<null>");
	            String tgt = su.getTarget().stream()
	                .filter(e -> e instanceof ActionUsage)
	                .map(e -> ((ActionUsage)e).getDeclaredName())
	                .findFirst().orElse("<null>");
	            System.out.printf("DEBUG Succession: %s → %s%n", src, tgt);
	        }
	        System.out.println("==========================");

	        assertHasSuccession("start",   "action1");
	        assertHasSuccession("action1", "action2");
	        assertHasSuccession("action2", "action3");
	        assertHasSuccession("action3", "action4");
	    }

	    
	    private static void assertHasSuccession(String src, String tgt) {
	        boolean found = successions.stream().anyMatch(su -> {
	            // cada SuccessionAsUsage pode ter múltiplas fontes/targets, pegamos o primeiro
	            String sourceName = su.getSource().stream()
	                .filter(e -> e instanceof ActionUsage)
	                .map(e -> ((ActionUsage)e).getDeclaredName())
	                .findFirst().orElse(null);
	            String targetName = su.getTarget().stream()
	                .filter(e -> e instanceof ActionUsage)
	                .map(e -> ((ActionUsage)e).getDeclaredName())
	                .findFirst().orElse(null);
	            return src.equals(sourceName) && tgt.equals(targetName);
	        });
	        assertTrue(found, "Esperava encontrar SuccessionAsUsage " + src + " → " + tgt);
	    }

	    private static List<SuccessionAsUsage> collectSuccessionAsUsages(Namespace ns) {
	        List<SuccessionAsUsage> list = new ArrayList<>();
	        TreeIterator<EObject> it = ns.eAllContents();
	        while (it.hasNext()) {
	            EObject e = it.next();
	            if (e instanceof SuccessionAsUsage) {
	                list.add((SuccessionAsUsage) e);
	            }
	        }
	        return list;
	    }
	}
