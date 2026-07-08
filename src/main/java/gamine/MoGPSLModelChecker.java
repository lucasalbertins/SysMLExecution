package gamine;

import gpsl.modelchecker.StepModelChecker;
import interfaces.behavior.actions.nodes.INode;
import obp3.runtime.sli.Step;

import org.omg.sysml.lang.sysml.Namespace;
import br.ufrpe.dc.sysml.SysMLV2Spec;
import adapters.behavior.actions.ActionUsageAdapter;
import adapters.behavior.actions.ActionUsageAdapterRegistry;
import gamine.SysMLV2ActionSemantics;
import gamine.domain.SysMLV2Configuration;

public class MoGPSLModelChecker {
	
	private static SysMLV2Spec spec;
    private static Namespace rootNamespace;
    private static ActionUsageAdapterRegistry registry;

    public static void init() {
        spec = new SysMLV2Spec();
        spec.parseFile("control/BatteryExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        System.out.println("NewDecisionNodeExample.sysml loaded");
        registry = new ActionUsageAdapterRegistry(rootNamespace);
    }
    
    // Creates a semantics from an ActionUsage.
    private static SysMLV2ActionSemantics createSemantics(String actionName) {
        ActionUsageAdapter usageAdapter = registry.getByDeclaredName(actionName).getFirst();
        return new SysMLV2ActionSemantics(usageAdapter);
    }
    
    public static boolean eval(String expr, Step<INode, SysMLV2Configuration> step) {
    		System.out.println("----- <expression = "+ expr + ", step = " + step + ">"); return true;
    }
    
    public static void main(String[] args) {
    		init();
        var semantics = createSemantics("chargeBattery");
        String prop = "prop=G |model.action.state.goes.to(34)| && |otherThing|";
        var checker = new StepModelChecker<>(
                semantics,
                MoGPSLModelChecker::eval, 
                prop);
        var result =  checker.modelChecker().runAlone();
        System.out.println(result);
    }
}

/// G (|batteryCharge' == batteryCharge + 1|)

/// Step: {
/// source: SysMLConfiguration(...batteryCharge = 80 ... successions={ 3228X239 } flows={} ) ,
/// action: INode (XBehAction) | Stutter
/// target: SysMLConfiguration(...batteryCharge = 81)
/// }


/// G F | S(UUID)  |   |S'(UUID)|
