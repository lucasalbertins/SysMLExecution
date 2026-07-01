package br.ufrpe.dc.sysml.gamine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.ActionUsageAdapter;
import adapters.behavior.actions.ActionUsageAdapterRegistry;
import br.ufrpe.dc.sysml.SysMLV2Spec;
import gamine.SysMLV2GPSLModelChecker;

@DisplayName("GPSL Model Checking - StepWrapper Features")
public class SysMLV2GPSLModelCheckerTest {

    private Namespace rootNamespace;
    private ActionUsageAdapterRegistry registry;

    @BeforeEach
    void init() {
        var spec = new SysMLV2Spec();
        spec.parseFile("control/NewDecisionNodeExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        registry = new ActionUsageAdapterRegistry(rootNamespace);
        System.out.println("\n===== SysML AST loaded for Wrapper testing =====");
    }

    private SysMLV2GPSLModelChecker gpslChecker(String action) {
        ActionUsageAdapter ua = registry.getByDeclaredName(action).getFirst();
        return new SysMLV2GPSLModelChecker(ua, rootNamespace);
    }

    @Nested
    @DisplayName("Transition-Based Tests (Source vs. Target)")
    class TransitionProperties {

    	@Test
        @DisplayName("Ensures the battery level never decreases (Always Target >= Source).")
        void batteryNeverDecreases() {
            var result = gpslChecker("chargeBattery")
                    .check("p =! [] |target.battery >= source.battery|");
            System.out.println(result.toString());
            assertFalse(result.holds(),
                "Expected: battery decreases from 110 to 100 in endCharging.");
        }

        @Test
        @DisplayName("Checks if a specific change occurs in the variable.")
        void verifySpecificChargeIncrement() {
            var result = gpslChecker("chargeBattery")
                    .check("p =! [] (|actionName == 'addCharge'| -> |target.battery == source.battery + 20.0|)");
            assertFalse(result.holds(),
                "It will fail if the actual increment is not exactly 20.0.");
        }
    }

    @Nested
    @DisplayName("Action and Event-Based Tests (ActionName)")
    class ActionProperties {

        @Test
        @DisplayName("Whenever 'endCharging' is executed, the 'isCharging' flag on the target is set to false.")
        void actionEffectsOnTarget() {
            var result = gpslChecker("chargeBattery")
                    .check("p =! [] (|actionName == 'endCharging'| -> |target.isCharging == false|)");
            //System.out.println(result.toString());
            assertTrue(result.holds(),
                "The execution of endCharging must have the immediate effect of turning off the flag.");
        }

        @Test
        @DisplayName("The 'addCharge' action must eventually be executed.")
        void specificActionEventuallyRuns() {
            var result = gpslChecker("chargeBattery")
                    .check("p =! <> |actionName == 'addCharge'|");
            assertTrue(result.holds(),
                "The charging cycle must pass through the load-addition node at least once.");
        }
    }
    
    @Nested
    @DisplayName("Topology Tests (Deadlock / Done)")
    class TopologicalProperties {

        @Test
        @DisplayName("The system must complete successfully.")
        void systemTerminates() {
            var result = gpslChecker("chargeBattery").check("p =! <> |done|");
            assertTrue(result.holds(),
                "The system cannot enter an infinite loop..");
        }
    }

    @Disabled
    @Nested
    @DisplayName("Compatibility with Legacy Properties (Implicit Target)")
    class BackwardCompatibility {

        @Test
        @DisplayName("Variables accessed directly refer to the Target.")
        void directVariableAccess() {
            var result = gpslChecker("chargeBattery").check("p =! [] |battery >= 30.0|");
            assertTrue(result.holds(),
                "The evaluator must continue to function with the original properties.");
        }
    }
}