package br.ufrpe.dc.sysml.gamine;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.ActionDefinitionAdapterRegistry;
import adapters.behavior.actions.ActionUsageAdapter;
import br.ufrpe.dc.sysml.SysMLV2Spec;
import gamine.SysMLV2ActionSemantics;
import obp3.Sequencer;
import obp3.sli.core.operators.SemanticRelation2RootedGraph;
import obp3.sli.core.operators.ToDetermistic;
import obp3.traversal.dfs.DepthFirstTraversal;

class SysMLV2SemanticsTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;
    private static ActionDefinitionAdapterRegistry registry;

    @BeforeAll
    static void init() {
        spec = new SysMLV2Spec();
        spec.parseFile("behavior/SimpleSuccession.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        System.out.println("SimpleSuccession.sysml loaded");
        assertNotNull(rootNamespace, "Namespace não deve ser nulo");
        registry = new ActionDefinitionAdapterRegistry(rootNamespace);
        assertNotNull(registry, "Registry não deve ser nulo");
    }

    // Busca recursiva genérica por nome e tipo
    private static <T extends Element> Optional<T> findElementByNameRecursive(
            Element element,
            String name,
            Class<T> type) {

        if (type.isInstance(element) && name.equals(element.getDeclaredName())) {
            return Optional.of(type.cast(element));
        }
        if (element instanceof Namespace ns) {
            for (Element child : ns.getOwnedMember()) {
                Optional<T> result =
                        findElementByNameRecursive(child, name, type);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        return Optional.empty();
    }

    // Cria um ActionUsageAdapter a partir do nome da ActionUsage
    private ActionUsageAdapter getActionUsageAdapter(String name) {
        ActionUsage action =
                findElementByNameRecursive(
                        rootNamespace,
                        name,
                        ActionUsage.class)
                .orElseThrow(() ->
                        new AssertionError("ActionUsage '" + name + "' não encontrada"));

        return new ActionUsageAdapter(action);
    }
    
    // Cria a semântica a partir de uma ActionUsage
    private SysMLV2ActionSemantics createSemantics(String actionName) {
        ActionUsageAdapter usageAdapter = getActionUsageAdapter(actionName);
        return new SysMLV2ActionSemantics(usageAdapter);
    }

    @Test
    void testDFS() {
        var semantics = createSemantics("test");
        var rootedGraph = new SemanticRelation2RootedGraph<>(semantics);
        var dfs = new DepthFirstTraversal<>(rootedGraph);
        var result = dfs.runAlone();
        System.out.println(result);
    }

//    @Test
//    void testDFSAgain() {
//        var semantics = createSemantics("test");
//        var rootedGraph = new SemanticRelation2RootedGraph<>(semantics);
//        var dfs = new DepthFirstTraversal<>(rootedGraph);
//        var result = dfs.runAlone();
//        System.out.println(result);
//    }

    @Test
    void testSequencer() {
        var semantics = createSemantics("test");
        var deterministic = ToDetermistic.randomPolicy(semantics, System.nanoTime());
        var sequencer = new Sequencer<>(deterministic);
        var result = sequencer.runAlone();
        System.out.println(result);
    }
}