package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.ActionDefinition;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.ActionDefinitionAdapter;
import adapters.behavior.actions.ActionDefinitionAdapterRegistry;
import br.ufrpe.dc.sysml.SysMLV2Spec;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.INamedElement;
import interfaces.utils.IParameter;

class ActionDefinitionAdapterTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;
    private static ActionDefinitionAdapterRegistry registry;

    @BeforeAll
    static void init() throws IOException {
        spec = new SysMLV2Spec();
        spec.parseFile("control/ForkJoinExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "Namespace não deve ser nulo");
        
        registry = new ActionDefinitionAdapterRegistry(rootNamespace);
        assertNotNull(registry, "Registry não deve ser nulo");
    }

    // Utils
//    private void collectAllActionDefinitions(Element elt, List<ActionDefinition> out) {
//        if (elt == null) return;
//
//        if (elt instanceof ActionDefinition ad) {
//            out.add(ad);
//        }
//
//        if (elt instanceof Namespace ns) {
//            for (Element member : ns.getOwnedMember()) {
//                collectAllActionDefinitions(member, out);
//            }
//        }
//    }
    
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
    private boolean hasParameter(ActionDefinitionAdapter adapter,
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
    
//    private static ActionUsage findFirstActionUsage(Element element) {
//
//        if (element instanceof ActionUsage au) {
//            return au;
//        }
//
//        if (element instanceof Namespace ns) {
//            for (Element member : ns.getOwnedMember()) {
//                ActionUsage found = findFirstActionUsage(member);
//                if (found != null) {
//                    return found;
//                }
//            }
//        }
//
//        return null;
//    }

    
    // Tests-------------------------------------------------------------
    @Test
    void testRegistry() {

//        // 1. Encontra uma ActionUsage qualquer no modelo
//        ActionUsage usage = findFirstActionUsage(rootNamespace);
//        assertNotNull(usage, "ActionUsage não deveria ser nula");
//        System.out.println(usage.getElementId());
//
//        // 2. Obtém a ActionDefinition associada à usage
//        ActionDefinition definition = (ActionDefinition) usage.getActionDefinition().getFirst();
//        assertNotNull(definition, "ActionUsage deve referenciar uma ActionDefinition");
//        System.out.println(definition.getDeclaredName());
//
//        String definitionId = definition.getElementId();
//        assertNotNull(definitionId, "ActionDefinition deve ter elementId");
//        System.out.println(definitionId);
        
        System.out.println(registry.getByDeclaredName("MonitorTraction").get(0).getID());
        for (INode node : registry.getByDeclaredName("Brake").get(0).getNodes()) {
        	System.out.println(node.getDeclaredName());
        }
        System.out.println();
        
        for (ActionDefinitionAdapter ad : registry.getAll()) {
        	System.out.println(ad.getDeclaredName());
        	System.out.println(ad.getID());
        }
    }
    
    @Test
    void testMonitorBrakePedalParameters() {
        ActionDefinition def =
            findElementByNameRecursive(
                rootNamespace,
                "MonitorBrakePedal",
                ActionDefinition.class
            ).orElseThrow(() ->
                new AssertionError(
                    "ActionDefinition 'MonitorBrakePedal' não encontrada"));

        ActionDefinitionAdapter adapter = registry.getById(def.getElementId());

        assertEquals("MonitorBrakePedal",
                adapter.getDeclaredName());

        assertEquals(1,
                adapter.getParameters().length,
                "MonitorBrakePedal deve ter exatamente 1 parâmetro");

        assertTrue(
                hasParameter(adapter, "pressure", "out"),
                "MonitorBrakePedal deve possuir parâmetro 'out pressure'");

        // Consistência estrutural
        assertTrue(adapter.getFlows().length == 0,
                "MonitorBrakePedal não deve possuir flows");
        
        assertTrue(adapter.getNodes().length == 0,
        		"MonitorBrakePedal não deve possuir nodes");
        
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
        ActionDefinition def =
            findElementByNameRecursive(
                rootNamespace,
                "MonitorTraction",
                ActionDefinition.class
            ).orElseThrow(() ->
                new AssertionError(
                    "ActionDefinition 'MonitorTraction' não encontrada"));

        //ActionDefinitionAdapter adapter = new ActionDefinitionAdapter(def);
        ActionDefinitionAdapter adapter = registry.getById(def.getElementId());

        assertEquals("MonitorTraction",
                adapter.getDeclaredName());

        assertEquals(1,
                adapter.getParameters().length,
                "MonitorTraction deve ter exatamente 1 parâmetro");

        assertTrue(
                hasParameter(adapter, "modFreq", "out"),
                "MonitorTraction deve possuir parâmetro 'out modFreq'");

        assertTrue(adapter.getFlows().length == 0,
        		"MonitorTraction não deve possuir flows");
        
        assertTrue(adapter.getNodes().length == 0,
        		"MonitorTraction não deve possuir nodes");
        
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
        ActionDefinition def =
            findElementByNameRecursive(
                rootNamespace,
                "Braking",
                ActionDefinition.class
            ).orElseThrow(() ->
                new AssertionError(
                    "ActionDefinition 'Braking' não encontrada"));

        //ActionDefinitionAdapter adapter = new ActionDefinitionAdapter(def);
        ActionDefinitionAdapter adapter = registry.getById(def.getElementId());

        assertEquals("Braking",
                adapter.getDeclaredName());

        assertEquals(2,
                adapter.getParameters().length,
                "Braking deve possuir exatamente 2 parâmetros");

        assertTrue(
                hasParameter(adapter, "brakePressure", "in"),
                "Braking deve possuir parâmetro 'in brakePressure'");

        assertTrue(
                hasParameter(adapter, "modulationFrequency", "in"),
                "Braking deve possuir parâmetro 'in modulationFrequency'");

        // Verifica que nenhum parâmetro está sem direção
        for (IParameter p : adapter.getParameters()) {
            assertNotNull(
                    p.getDirection(),
                    "Parâmetro '" + p.getDeclaredName()
                    + "' não deve ter direção nula");
        }
        assertTrue(adapter.getFlows().length == 0,
        		"Braking não deve possuir flows");

        assertTrue(adapter.getNodes().length == 0,
        		"Braking não deve possuir nodes");
    }
    
    @Test
    void testBrakeParameters() {
        ActionDefinition def =
            findElementByNameRecursive(
                rootNamespace,
                "Brake",
                ActionDefinition.class
            ).orElseThrow(() ->
                new AssertionError(
                    "ActionDefinition 'Brake' não encontrada"));

        //ActionDefinitionAdapter adapter = new ActionDefinitionAdapter(def);
        ActionDefinitionAdapter adapter = registry.getById(def.getElementId());

        assertEquals("Brake",
                adapter.getDeclaredName());

        assertEquals(0,
                adapter.getParameters().length,
                "Brake deve possuir exatamente 0 parâmetros");
        
        // Verifica que nenhum parâmetro está sem direção
        for (IParameter p : adapter.getParameters()) {
            assertNotNull(
                    p.getDirection(),
                    "Parâmetro '" + p.getDeclaredName()
                    + "' não deve ter direção nula");
        }
        assertTrue(adapter.getFlows().length == 2,
        		"Braking deve possuir 2 flows");

        assertTrue(adapter.getNodes().length == 8,
        		"Braking deve possuir 8 nodes");
    }
    
    
    @Test
    void testActionDefinitionAdaptersStructureAndConsistency() {
        //List<ActionDefinition> actionDefs = new ArrayList<>();
        //collectAllActionDefinitions(rootNamespace, actionDefs);

        assertFalse(registry.getAll().isEmpty(), "Nenhuma ActionDefinition encontrada no modelo");

        for (ActionDefinitionAdapter adapter : registry.getAll()) {

            //ActionDefinitionAdapter adapter = new ActionDefinitionAdapter(actionDef);
        	//ActionDefinitionAdapter adapter = registry.getById(actionDef.getElementId());
            
            // Nome
            assertNotNull(adapter.getDeclaredName(),
                    "ActionDefinitionAdapter deve ter nome declarado");
            
            // Parâmetros
            IParameter[] parameters = adapter.getParameters();
            assertNotNull(parameters,
                    "Array de parâmetros não deve ser nulo");

            for (IParameter parameter : parameters) {
                assertNotNull(parameter.getDeclaredName(),
                        "Parâmetro não deve ter nome nulo");

                assertNotNull(parameter.getDirection(),
                        "Parâmetro '" + parameter.getDeclaredName()
                        + "' deve possuir direção (in/out/inout)");
            }

            // Nodes
            INode[] nodes = adapter.getNodes();
            assertNotNull(nodes,
                    "Array de nodes não deve ser nulo");

            for (INode node : nodes) {
                assertNotNull(node.getDeclaredName(),
                        "Node não deve ter nome nulo");
            }

            // Flows
            IFlow[] flows = adapter.getFlows();
            assertNotNull(flows,
                    "Array de flows não deve ser nulo");

            for (IFlow flow : flows) {
                assertNotNull(flow.getDeclaredName(),
                        "Flow deve possuir nome declarado");

                assertNotNull(flow.getSource(),
                        "Flow '" + flow.getDeclaredName()
                        + "' deve possuir source");

                assertNotNull(flow.getTarget(),
                        "Flow '" + flow.getDeclaredName()
                        + "' deve possuir target");

                // Validação estrutural do caminho
                assertDoesNotThrow(() -> toPath(flow.getSource()),
                        "Falha ao montar path do source do flow");

                assertDoesNotThrow(() -> toPath(flow.getTarget()),
                        "Falha ao montar path do target do flow");
            }
            
            // MonitorBattery
            if (adapter.getDeclaredName().equals("MonitorBattery")) {
                IParameter[] params = adapter.getParameters();
                assertEquals(1, params.length,
                        "MonitorBattery deve ter exatamente 1 parâmetro");

                IParameter p = params[0];
                assertEquals("charge", p.getDeclaredName());
                assertEquals("out", p.getDirection().toString().toLowerCase());
            }

            // AddCharge
            if (adapter.getDeclaredName().equals("AddCharge")) {
                IParameter[] params = adapter.getParameters();
                assertEquals(1, params.length,
                        "AddCharge deve ter exatamente 1 parâmetro");

                IParameter p = params[0];
                assertEquals("charge", p.getDeclaredName());
                assertEquals("in", p.getDirection().toString().toLowerCase());
            }

            // EndCharging
            if (adapter.getDeclaredName().equals("EndCharging")) {
                assertEquals(0, adapter.getParameters().length,
                        "EndCharging não deve possuir parâmetros");
            }

            // ChargeBattery
            if (adapter.getDeclaredName().equals("ChargeBattery")) {

                List<String> nodeNames = new ArrayList<>();
                for (INode node : adapter.getNodes()) {
                    nodeNames.add(node.getDeclaredName());
                }

                assertTrue(nodeNames.contains("start"),
                        "ChargeBattery deve possuir node 'start'");

                assertTrue(nodeNames.contains("continueCharging"),
                        "ChargeBattery deve possuir merge 'continueCharging'");

                assertTrue(nodeNames.contains("decision1"),
                        "ChargeBattery deve possuir decision 'decision1'");

                assertTrue(nodeNames.contains("done"),
                        "ChargeBattery deve possuir node 'done'");
            }
            
            
            // Print
            
            System.out.println("\n=== Testing ActionDefinitionAdapter for: " + adapter.getDeclaredName() + " ===");
    		System.out.println("Parameters:");
    		if (adapter.getParameters().length != 0) {
    			for (IParameter parameter : adapter.getParameters()) {
    				System.out.print(parameter.getDirection() != null ? parameter.getDirection() + " " : "<null> ");
        			System.out.println(parameter.getDeclaredName() != null ? parameter.getDeclaredName() : "<null>");
    			}
    		} else {
    			System.out.println("<no-parameters>");
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