package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.impl.ActionUsageImpl;

import adapters.behavior.actions.ActionDefinitionAdapter;
import adapters.behavior.actions.ActionDefinitionAdapterRegistry;
import adapters.behavior.actions.ActionUsageAdapter;
import br.ufrpe.dc.sysml.SysMLV2Spec;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.INamedElement;
import interfaces.utils.IParameter;

class ActionUsageAdapterTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;
    private static ActionDefinitionAdapterRegistry registry;

    @BeforeAll
    static void init() throws IOException {
        spec = new SysMLV2Spec();
        spec.parseFile("control/ForkJoinExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "Namespace raiz não deve ser nulo");
        
        registry = new ActionDefinitionAdapterRegistry(rootNamespace);
        assertNotNull(registry, "Registry não deve ser nulo");
    }

    // Utils (consegui filtrar para apenas instâncias exatas de ActionUsage)
    private void collectAllActionUsages(Element elt, List<ActionUsage> out) {

        if (elt.getClass().equals(ActionUsageImpl.class)) {
            out.add((ActionUsage) elt);
        }

        if (elt instanceof Namespace ns) {
            for (Element member : ns.getOwnedMember()) {
                collectAllActionUsages(member, out);
            }
        }
    }
    
    // Busca recursiva genérica por nome e tipo
    private static <T extends Element> Optional<T> findElementByNameRecursive(
            Element element, String name, Class<T> type) {

        if (type.isInstance(element)
                && name.equals(element.getDeclaredName())) {
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

    // Verifica semanticamente se existe parâmetro com nome e direção
    private boolean hasParameter(ActionUsageAdapter adapter,
                                 String paramName,
                                 String direction) {

        for (IParameter p : adapter.getParameters()) {
            if (paramName.equals(p.getDeclaredName())
                    && direction.equalsIgnoreCase(p.getDirection().toString())) {
                return true;
            }
        }
        return false;
    }

    // Imprime o caminho do source/target de FlowUsage
    private String toPath(IFlowEnd end) {
        StringBuilder sb = new StringBuilder();
        if (end.getReferencedFeature() != null) {
            sb.append(end.getReferencedFeature().getDeclaredName()).append(".");
        }
        for (INamedElement ine : end.getChainingFeatures()) {
            sb.append(ine.getName()).append(".");
        }
        sb.append(end.getReferenceUsage().getName());
        return sb.toString();
    }

    
    // Tests
    @Test
    void testMonitorBrakePedalParameters() {
        ActionUsage action =
            findElementByNameRecursive(
                rootNamespace,
                "monitorBrakePedal",
                ActionUsage.class
            ).orElseThrow(() ->
                new AssertionError(
                    "ActionUsage 'monitorBrakePedal' não encontrada"));

        ActionUsageAdapter adapter =
                new ActionUsageAdapter(action);
        
        System.out.println("Exemplo");
        ActionDefinitionAdapter def = registry.getById(action.getActionDefinition().getFirst().getElementId());
        for (IParameter parameter : def.getParameters()) {
        	System.out.println(parameter.getDirection() + " " + parameter.getDeclaredName());
        }

        assertEquals("monitorBrakePedal",
                adapter.getDeclaredName());

        assertEquals(1,
                adapter.getParameters().length,
                "monitorBrakePedal deve ter exatamente 1 parâmetro");

        assertTrue(
                hasParameter(adapter, "brakePressure", "out"),
                "monitorBrakePedal deve possuir parâmetro 'out brakePressure'");

        assertTrue(adapter.getFlows().length == 0,
        		"monitorBrakePedal não deve possuir flows");
        
        assertTrue(adapter.getNodes().length == 0,
        		"monitorBrakePedal não deve possuir nodes");
        
        // Verifica que nenhum parâmetro está sem direção
        for (IParameter p : adapter.getParameters()) {
            assertNotNull(
                    p.getDirection(),
                    "Parâmetro '" + p.getDeclaredName()
                    + "' não deve ter direção nula");
        }
     
    }
    
    @Test
    void testMonitorTractionParameters() {
        ActionUsage action =
            findElementByNameRecursive(
                rootNamespace,
                "monitorTraction",
                ActionUsage.class
            ).orElseThrow(() ->
                new AssertionError(
                    "ActionUsage 'monitorTraction' não encontrada"));

        ActionUsageAdapter adapter =
                new ActionUsageAdapter(action);

        assertEquals("monitorTraction",
                adapter.getDeclaredName());

        assertEquals(1,
                adapter.getParameters().length,
                "monitorTraction deve ter exatamente 1 parâmetro");

        assertTrue(
                hasParameter(adapter, "modulationFrequency", "out"),
                "monitorTraction deve possuir parâmetro 'out modulationFrequency'");

        assertTrue(adapter.getFlows().length == 0,
        		"monitorTraction não deve possuir flows");
        
        assertTrue(adapter.getNodes().length == 0,
        		"monitorTraction não deve possuir nodes");
        
        // Verifica que nenhum parâmetro está sem direção
        for (IParameter p : adapter.getParameters()) {
            assertNotNull(
                    p.getDirection(),
                    "Parâmetro '" + p.getDeclaredName()
                    + "' não deve ter direção nula");
        }
     
    }
    
    @Test
    void testBrakingParameters() {
        ActionUsage action =
            findElementByNameRecursive(
                rootNamespace,
                "braking",
                ActionUsage.class
            ).orElseThrow(() ->
                new AssertionError(
                    "ActionUsage 'braking' não encontrada"));

        ActionUsageAdapter adapter =
                new ActionUsageAdapter(action);

        assertEquals("braking",
                adapter.getDeclaredName());

        assertEquals(2,
                adapter.getParameters().length,
                "braking deve possuir exatamente 2 parâmetros");

        assertTrue(
                hasParameter(adapter, "brakePressure", "in"),
                "braking deve possuir parâmetro 'in brakePressure'");

        assertTrue(
                hasParameter(adapter, "modulationFrequency", "in"),
                "braking deve possuir parâmetro 'in modulationFrequency'");

        // Verifica que nenhum parâmetro está sem direção
        for (IParameter p : adapter.getParameters()) {
            assertNotNull(
                    p.getDirection(),
                    "Parâmetro '" + p.getDeclaredName()
                    + "' não deve ter direção nula");
        }

        assertTrue(adapter.getFlows().length == 0,
        		"braking não deve possuir flows");

        assertTrue(adapter.getNodes().length == 0,
        		"braking não deve possuir nodes");
    }
    
    @Test
    void testActionUsageAdaptersStructureAndConsistency() {
        List<ActionUsage> actionUsages = new ArrayList<>();
        collectAllActionUsages(rootNamespace, actionUsages);

        assertFalse(actionUsages.isEmpty(),
                "Nenhuma ActionUsage encontrada no modelo");

        for (ActionUsage actionUsage : actionUsages) {

            ActionUsageAdapter adapter = new ActionUsageAdapter(actionUsage);
            
            // Print
            System.out.println("\n=== Testing ActionUsage for: " + adapter.getDeclaredName() + " ===");
            System.out.println("Definition:");
            System.out.println(adapter.getActionDefinition() != null ? 
            		adapter.getActionDefinition().getDeclaredName() : "<no-definition>");
            
            System.out.println("\nParameters:");
            if (adapter.getInputs().length == 0 && adapter.getOutputs().length == 0) {
                System.out.println("<no-parameters>");
            }

            if (adapter.getInputs().length != 0) {
            	System.out.println("Inputs:");
                for (IParameter p : adapter.getInputs()) {
                    System.out.println(
                        (p.getDirection() != null ? p.getDirection() : "<null>") + " " +
                        (p.getDeclaredName() != null ? p.getDeclaredName() : "<null>")
                    );
                }
            }

            if (adapter.getOutputs().length != 0) {
            	System.out.println("Outputs:");
                for (IParameter p : adapter.getOutputs()) {
                   System.out.println(
                        (p.getDirection() != null ? p.getDirection() : "<null>") + " " +
                        (p.getDeclaredName() != null ? p.getDeclaredName() : "<null>")
                    );
                }
            }
    		
    		System.out.println("\nFlows:");
    		if (adapter.getFlows().length != 0) {
    			for (IFlow flow : adapter.getFlows()) {
        			System.out.println(flow.getDeclaredName());
        			System.out.println("from " + toPath(flow.getSource()));
        			System.out.println("to " + toPath(flow.getTarget()));
        		}
    		} else {
    			System.out.println("<no-flows>");
    		}
    		
    		System.out.println("\nNodes:");
    		if (adapter.getNodes().length != 0) {
    			for (INode node : adapter.getNodes()) {
    				System.out.println(node.getDeclaredName());
        		}
    		} else {
    			System.out.println("<no-nodes>");
    		} 
    		
        }
    }
}