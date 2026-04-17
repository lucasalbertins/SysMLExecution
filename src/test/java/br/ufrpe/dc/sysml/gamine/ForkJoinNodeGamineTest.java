package br.ufrpe.dc.sysml.gamine;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.ActionUsageAdapter;
import adapters.behavior.actions.ActionUsageAdapterRegistry;
import br.ufrpe.dc.sysml.SysMLV2Spec;

import gamine.SysMLV2ActionSemantics;
import obp3.Sequencer;
import obp3.sli.core.operators.SemanticRelation2RootedGraph;
import obp3.sli.core.operators.ToDetermistic;
import obp3.traversal.dfs.DepthFirstTraversal;

public class ForkJoinNodeGamineTest {
	
	private static SysMLV2Spec spec;
    private static Namespace rootNamespace;
    private static ActionUsageAdapterRegistry registry;

    @BeforeAll
    static void init() {
        spec = new SysMLV2Spec();
        spec.parseFile("control/ForkJoinExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        System.out.println("ForkJoinExample.sysml loaded");
        assertNotNull(rootNamespace, "Namespace cannote be null.");
        registry = new ActionUsageAdapterRegistry(rootNamespace);
        assertNotNull(registry, "Registry cannot be null.");
    }

    // Creates a semantics from an ActionUsage.
    private SysMLV2ActionSemantics createSemantics(String actionName) {
        ActionUsageAdapter usageAdapter = registry.getByDeclaredName(actionName).getFirst();
        return new SysMLV2ActionSemantics(usageAdapter);
    }
    // Produces all outgoings from the ForkNode, navigating through them one by one in sequence (random order).
    // After that, consumes all incomings in the JoinNode.

//    @Test
//    void testDFS() {
//        var semantics = createSemantics("brake");
//        var rootedGraph = new SemanticRelation2RootedGraph<>(semantics);
//        var dfs = new DepthFirstTraversal<>(rootedGraph);
//        var result = dfs.runAlone();
//        System.out.println(result);
//    }
    
    @Test
    void newTest() {
    	var semantics = createSemantics("brake");
        //var deterministic = ToDetermistic.firstPolicy(semantics);
        //var deterministic = ToDetermistic.lastPolicy(semantics);
        var deterministic = ToDetermistic.randomPolicy(semantics, System.nanoTime());

        var sequencer = new Sequencer<>(deterministic);
        int[] count = new int[]{10};

        var result = sequencer.run(c -> {
            System.out.println("---- " + c);
            return count[0]-- <= 1;
        });
    }
}
