package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.ActionDefinition;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.ActionDefinitionAdapter;
import adapters.behavior.actions.ActionDefinitionAdapterRegistry;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.INamedElement;
import interfaces.utils.IParameter;

import br.ufrpe.dc.sysml.SysMLV2Spec;

public class ActionDefinitionAdapterTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;
    private static ActionDefinitionAdapterRegistry registry;

    @BeforeAll
    static void init() throws IOException {
        spec = new SysMLV2Spec();
        spec.parseFile("control/ForkJoinExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "Namespace cannot be null.");
        registry = new ActionDefinitionAdapterRegistry(rootNamespace);
        assertNotNull(registry, "Registry cannot be null.");
    }
    
    // Generic recursive search by name and type.
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

    // Semantically checks if a parameter with a name and direction exists.
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

    // Prints the source/target path of the FlowUsage.
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
    
    @Test
    void testRegistry() {
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
                    "ActionDefinition 'MonitorBrakePedal' not found."));

        ActionDefinitionAdapter adapter = registry.getById(def.getElementId());
        assertEquals("MonitorBrakePedal",
                adapter.getDeclaredName());
        assertEquals(1,
                adapter.getParameters().length,
                "MonitorBrakePedal must have exactly 1 parameter.");
        assertTrue(
                hasParameter(adapter, "pressure", "out"),
                "MonitorBrakePedal must have an 'out pressure' parameter.");
        // Structural consistency.
        assertTrue(adapter.getFlows().length == 0,
                "MonitorBrakePedal should not contain flows.");
        assertTrue(adapter.getNodes().length == 0,
        		"MonitorBrakePedal should not contain nodes.");
        
        // Verifies that no parameter is directionless.
        for (IParameter p : adapter.getParameters()) {
            assertNotNull(
                    p.getDirection(),
                    "Parameter '" + p.getDeclaredName()
                    + "' must not have a null direction.");
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
                    "ActionDefinition 'MonitorTraction' not found."));

        ActionDefinitionAdapter adapter = registry.getById(def.getElementId());
        assertEquals("MonitorTraction",
                adapter.getDeclaredName());
        assertEquals(1,
                adapter.getParameters().length,
                "MonitorTraction must have exactly 1 parameter.");
        assertTrue(
                hasParameter(adapter, "modFreq", "out"),
                "MonitorTraction must have an 'out modFreq' parameter.");
        assertTrue(adapter.getFlows().length == 0,
        		"MonitorTraction should not contain flows.");
        assertTrue(adapter.getNodes().length == 0,
        		"MonitorTraction should not contain nodes.");
        
        // Verifies that no parameter is directionless.
        for (IParameter p : adapter.getParameters()) {
            assertNotNull(
                    p.getDirection(),
                    "Parameter '" + p.getDeclaredName()
                    + "' must not have a null direction.");
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
                    "ActionDefinition 'Braking' not found."));

        ActionDefinitionAdapter adapter = registry.getById(def.getElementId());
        assertEquals("Braking",
                adapter.getDeclaredName());
        assertEquals(2,
                adapter.getParameters().length,
                "Braking must have exactly 2 parameters.");
        assertTrue(
                hasParameter(adapter, "brakePressure", "in"),
                "Braking should contain an 'in brakePressure' parameter.");
        assertTrue(
                hasParameter(adapter, "modulationFrequency", "in"),
                "Braking should contain an 'in modulationFrequency' parameter.");

        // Verifies that no parameter is directionless.
        for (IParameter p : adapter.getParameters()) {
            assertNotNull(
                    p.getDirection(),
                    "Parâmetro '" + p.getDeclaredName()
                    + "' must not have a null direction.");
        }
        assertTrue(adapter.getFlows().length == 0,
        		"Braking should not contain flows.");

        assertTrue(adapter.getNodes().length == 0,
        		"Braking should not contain nodes.");
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
                    "ActionDefinition 'Brake' not found."));

        ActionDefinitionAdapter adapter = registry.getById(def.getElementId());
        assertEquals("Brake",
                adapter.getDeclaredName());
        assertEquals(0,
                adapter.getParameters().length,
                "Brake must have exactly 0 parameters");
        
        // Verifies that no parameter is directionless.
        for (IParameter p : adapter.getParameters()) {
            assertNotNull(
                    p.getDirection(),
                    "Parameter '" + p.getDeclaredName()
                    + "' must not have a null direction.");
        }
        assertTrue(adapter.getFlows().length == 2,
        		"Braking should contain 2 flows.");
        assertTrue(adapter.getNodes().length == 8,
        		"Braking should contain 8 nodes.");
    }
    
    @Test
    void testActionDefinitionAdaptersStructureAndConsistency() {
        assertFalse(registry.getAll().isEmpty(), "No ActionDefinition found in the model.");

        for (ActionDefinitionAdapter adapter : registry.getAll()) {
        	// Name
            assertNotNull(adapter.getDeclaredName(),
                    "ActionDefinitionAdapter must have a declared name.");
            
            // Parameters
            IParameter[] parameters = adapter.getParameters();
            assertNotNull(parameters,
                    "The parameter array must not be null.");
            for (IParameter parameter : parameters) {
                assertNotNull(parameter.getDeclaredName(),
                        "The parameter must not have a null name.");
                assertNotNull(parameter.getDirection(),
                        "Parameter '" + parameter.getDeclaredName()
                        + "' must have direction (in/out/inout).");
            }
            
            // Nodes
            INode[] nodes = adapter.getNodes();
            assertNotNull(nodes,
                    "The node array must not be null.");
            
            for (INode node : nodes) {
                assertNotNull(node.getDeclaredName(),
                    "The node must not have a null name.");
            }

            // Flows
            IFlow[] flows = adapter.getFlows();
            assertNotNull(flows,
                    "The flow array must not be null.");
            for (IFlow flow : flows) {
                assertNotNull(flow.getDeclaredName(),
                        "Flow must have a declared name.");
                assertNotNull(flow.getSource(),
                        "Flow '" + flow.getDeclaredName()
                        + "' must have a source.");
                assertNotNull(flow.getTarget(),
                        "Flow '" + flow.getDeclaredName()
                        + "' must have a target.");
                // Structural path validation.
                assertDoesNotThrow(() -> toPath(flow.getSource()),
                        "Failed to assemble flow source path.");

                assertDoesNotThrow(() -> toPath(flow.getTarget()),
                        "Failed to assemble flow target path.");
            }
            
            // MonitorBattery
            if (adapter.getDeclaredName().equals("MonitorBattery")) {
                IParameter[] params = adapter.getParameters();
                assertEquals(1, params.length,
                        "MonitorBattery must have exactly 1 parameter.");

                IParameter p = params[0];
                assertEquals("charge", p.getDeclaredName());
                assertEquals("out", p.getDirection().toString().toLowerCase());
            }
            
            // AddCharge
            if (adapter.getDeclaredName().equals("AddCharge")) {
                IParameter[] params = adapter.getParameters();
                assertEquals(1, params.length,
                        "AddCharge must have exatcly 1 parameter.");
                IParameter p = params[0];
                assertEquals("charge", p.getDeclaredName());
                assertEquals("in", p.getDirection().toString().toLowerCase());
            }
            
            // EndCharging
            if (adapter.getDeclaredName().equals("EndCharging")) {
                assertEquals(0, adapter.getParameters().length,
                        "EndCharging should not contain parameters.");
            }

            // ChargeBattery
            if (adapter.getDeclaredName().equals("ChargeBattery")) {
                List<String> nodeNames = new ArrayList<>();
                for (INode node : adapter.getNodes()) {
                    nodeNames.add(node.getDeclaredName());
                }
                assertTrue(nodeNames.contains("start"),
                        "ChargeBattery should contain node 'start'");
                assertTrue(nodeNames.contains("continueCharging"),
                        "ChargeBattery should contain merge 'continueCharging'");
                assertTrue(nodeNames.contains("decision1"),
                        "ChargeBattery should contain decision 'decision1'");
                assertTrue(nodeNames.contains("done"),
                        "ChargeBattery should contain node 'done'");
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
