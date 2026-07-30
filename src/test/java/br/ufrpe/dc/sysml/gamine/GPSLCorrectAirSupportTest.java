package br.ufrpe.dc.sysml.gamine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.sv2pi.behavior.actions.ActionUsageAdapter;
import adapters.sv2pi.behavior.actions.ActionUsageAdapterRegistry;
import adapters.sv2pi.utils.SysMLV2MemoryExtractor;
import modelchecker.SysMLV2GPSLModelChecker;
import parser.SysMLV2Spec;

public class GPSLCorrectAirSupportTest {

	private Namespace rootNamespace;
    private ActionUsageAdapterRegistry registry;
    private Map<String, Object> initialMemory;

    @BeforeEach
    void init() {
        var spec = new SysMLV2Spec();
        spec.parseFile("control/CorrectCloseAirSupport.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        registry = new ActionUsageAdapterRegistry(rootNamespace);
        initialMemory = SysMLV2MemoryExtractor.extract(rootNamespace);
        System.out.println("\n===== SysML AST loaded for Wrapper testing =====");
    }

    private SysMLV2GPSLModelChecker gpslChecker(String action) {
        ActionUsageAdapter ua = registry.getByDeclaredName(action).getFirst();
        return new SysMLV2GPSLModelChecker(ua, initialMemory);
    }
    
    @Test
    void systemTerminates() {
    	var result = gpslChecker("executeMission").check("p =! <> |done|");
        System.out.println(result.toString());
        assertTrue(result.holds(), "The system must reach the final node.");
    }
}
