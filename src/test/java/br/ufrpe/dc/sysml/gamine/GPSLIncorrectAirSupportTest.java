package br.ufrpe.dc.sysml.gamine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.sv2pi.behavior.actions.ActionUsageAdapter;
import adapters.sv2pi.behavior.actions.ActionUsageAdapterRegistry;
import modelchecker.SysMLV2GPSLModelChecker;
import parser.SysMLV2Spec;

public class GPSLIncorrectAirSupportTest {

	private Namespace rootNamespace;
    private ActionUsageAdapterRegistry registry;

    @BeforeEach
    void init() {
        var spec = new SysMLV2Spec();
        spec.parseFile("control/IncorrectCloseAirSupport.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        registry = new ActionUsageAdapterRegistry(rootNamespace);
        System.out.println("\n===== SysML AST loaded for Wrapper testing =====");
    }

    private SysMLV2GPSLModelChecker gpslChecker(String action) {
        ActionUsageAdapter ua = registry.getByDeclaredName(action).getFirst();
        return new SysMLV2GPSLModelChecker(ua, rootNamespace);
    }
    
    @Test
    void systemTerminates() {
        var result = gpslChecker("executeMission").check("p =! <> |done|");
        System.out.println(result.toString());
        assertFalse(result.holds(), "The system must not reach the final node.");
    }
}
