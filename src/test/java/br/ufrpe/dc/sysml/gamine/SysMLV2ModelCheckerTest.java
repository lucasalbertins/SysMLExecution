package br.ufrpe.dc.sysml.gamine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach; // JUnit puro
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Namespace;
import adapters.behavior.actions.ActionUsageAdapter;
import adapters.behavior.actions.ActionUsageAdapterRegistry;
import br.ufrpe.dc.sysml.SysMLV2Spec;
import gamine.SysMLV2ModelChecker;

public class SysMLV2ModelCheckerTest {

    private Namespace rootNamespace;
    private ActionUsageAdapterRegistry registry;

    // roda o parser a cada teste
    @BeforeEach
    void init() {
        var spec = new SysMLV2Spec();
        spec.parseFile("control/BatteryExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        registry = new ActionUsageAdapterRegistry(rootNamespace);
        System.out.println("===== New SysML AST loaded for testing =====");
    }

    private SysMLV2ModelChecker checker(String action) {
        ActionUsageAdapter ua = registry.getByDeclaredName(action).getFirst();
        return new SysMLV2ModelChecker(ua, rootNamespace);
    }
    
    @Test
    void chargingEventuallyStops() {
    	var result = checker("chargeBattery").checkEventually("isCharging == false");
    	System.out.println(result);
    	assertTrue(result.holds(), "The system must eventually stop charging");
    }

    @Test
    void batteryExceedsLimit_expectViolation() {
        var result = checker("chargeBattery").checkNever("battery > 100.0");
        System.out.println(result);
        assertFalse(result.holds(), "Expected: battery exceeds 100.");
    }

    @Test
    void batteryEventuallyFull() {
        var result = checker("chargeBattery").checkEventually("battery >= 100.0");
        System.out.println(result);
        assertTrue(result.holds(), "Battery must eventually reach 100.0");
    }

    @Test
    void systemEventuallyTerminates() {
        var result = checker("chargeBattery").checkEventually("done");
        System.out.println(result);
        assertTrue(result.holds(), "chargeBattery must eventually terminate");
    }

    @Test
    void batteryAlways80_expectViolation() {
        var result = checker("chargeBattery").checkNever("battery >= 85.0");
        System.out.println(result);
        assertFalse(result.holds(), "Counterexample expected: battery reaches 85.0");
    }
    
    @Test
    void batteryAlwaysPositive() {
        var result = checker("chargeBattery").checkAlways("battery >= 0.0");
        System.out.println(result);
        assertTrue(result.holds(), "Battery must always be positive");
    }

    @Test
    void batteryAlwaysBelow100_expectViolation() {
        var result = checker("chargeBattery").checkAlways("battery < 100.0");
        System.out.println(result);
        assertFalse(result.holds(), "Counterexample expected: battery reaches 110.0");
    }
}