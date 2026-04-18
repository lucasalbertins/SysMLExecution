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

public class ActionUsageAdapterTest {

    private static SysMLV2Spec spec;
    private static Namespace rootNamespace;
    private static ActionDefinitionAdapterRegistry registry;

    @BeforeAll
    static void init() throws IOException {
        spec = new SysMLV2Spec();
        spec.parseFile("control/ForkJoinExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        assertNotNull(rootNamespace, "The root namespace must not be null.");
        
        registry = new ActionDefinitionAdapterRegistry(rootNamespace);
        assertNotNull(registry, "Registry must not be null.");
    }

    // Utils (I managed to filter to only exact instances of ActionUsage)
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

    // Prints the source/target path of a FlowUsage.
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
    void testMonitorBrakePedalParameters() {
        ActionUsage action =
            findElementByNameRecursive(
                rootNamespace,
                "monitorBrakePedal",
                ActionUsage.class
            ).orElseThrow(() ->
                new AssertionError(
                    "ActionUsage 'monitorBrakePedal' not found."));

        ActionUsageAdapter adapter =  new ActionUsageAdapter(action);
        
        System.out.println("Example");
        ActionDefinitionAdapter def = registry.getById(action.getActionDefinition().getFirst().getElementId());
        for (IParameter parameter : def.getParameters()) {
        	System.out.println(parameter.getDirection() + " " + parameter.getDeclaredName());
        }

        assertEquals("monitorBrakePedal",
                adapter.getDeclaredName());
        assertEquals(1,
                adapter.getParameters().length,
                "monitorBrakePedal must have exactly 1 parameter.");
        assertTrue(
                hasParameter(adapter, "brakePressure", "out"),
                "monitorBrakePedal must have an 'out brakePressure' parameter.");
        assertTrue(adapter.getFlows().length == 0,
        		"monitorBrakePedal should not contain flows.");
        assertTrue(adapter.getNodes().length == 0,
        		"monitorBrakePedal should not contain nodes.");
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
        ActionUsage action =
            findElementByNameRecursive(
                rootNamespace,
                "monitorTraction",
                ActionUsage.class
            ).orElseThrow(() ->
                new AssertionError(
                    "ActionUsage 'monitorTraction' not found."));

        ActionUsageAdapter adapter = new ActionUsageAdapter(action);

        assertEquals("monitorTraction",
                adapter.getDeclaredName());
        assertEquals(1,
                adapter.getParameters().length,
                "monitorTraction must have exactly 1 parameter.");

        assertTrue(
                hasParameter(adapter, "modulationFrequency", "out"),
                "monitorTraction must have an 'out modulationFrequency' parameter.");

        assertTrue(adapter.getFlows().length == 0,
        		"monitorTraction should not contain flows.");
        
        assertTrue(adapter.getNodes().length == 0,
        		"monitorTraction should not contain nodes.");
        
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
        ActionUsage action =
            findElementByNameRecursive(
                rootNamespace,
                "braking",
                ActionUsage.class
            ).orElseThrow(() ->
                new AssertionError(
                    "ActionUsage 'braking' not found."));

        ActionUsageAdapter adapter = new ActionUsageAdapter(action);

        assertEquals("braking",
                adapter.getDeclaredName());
        assertEquals(2,
                adapter.getParameters().length,
                "braking must have exactly 2 parameters.");
        assertTrue(
                hasParameter(adapter, "brakePressure", "in"),
                "braking must have an 'in brakePressure' parameter.");
        assertTrue(
                hasParameter(adapter, "modulationFrequency", "in"),
                "braking must have an 'in modulationFrequency' parameter.");
        
        // Verifies that no parameter is directionless.
        for (IParameter p : adapter.getParameters()) {
            assertNotNull(
                    p.getDirection(),
                    "Parameter '" + p.getDeclaredName()
                    + "' must not have a null direction.");
        }
        assertTrue(adapter.getFlows().length == 0,
        		"braking should not contain flows.");
        assertTrue(adapter.getNodes().length == 0,
        		"braking should not contain nodes.");
    }
    
    @Test
    void testActionUsageAdaptersStructureAndConsistency() {
        List<ActionUsage> actionUsages = new ArrayList<>();
        collectAllActionUsages(rootNamespace, actionUsages);

        assertFalse(actionUsages.isEmpty(),
                "No ActionUsage found in the model.");

        for (ActionUsage actionUsage : actionUsages) {

            ActionUsageAdapter adapter = new ActionUsageAdapter(actionUsage);
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
