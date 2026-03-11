package adapters.utils;

import java.util.HashMap;
import java.util.List;

import org.omg.sysml.lang.sysml.ControlNode;
import org.omg.sysml.lang.sysml.FlowUsage;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;

import adapters.behavior.actions.SuccessionAdapter;
import adapters.behavior.actions.nodes.NodeAdapter;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class AdapterUtils {
	public static enum FlowType { Succession_As_Usage, Flow_Usage }
	
	public static HashMap<String,ISuccession> successions = new HashMap<>();

	public static FlowType flowType(SuccessionAdapter sa, List<String> searched) {
		INode previous = sa.getSource();
		
		if (previous instanceof FlowUsage) {
			return FlowType.Flow_Usage;
		}
		else if (previous instanceof ControlNode) {
			return FlowType.Succession_As_Usage;
		}
		return null;
	}
	
	public static ISuccession setSuccession(SuccessionAsUsage succession, String direction, NodeAdapter nodeAdapter) {
		if (!successions.containsKey(succession.getElementId())) {

	        ISuccession newSuccession = new SuccessionAdapter(succession);
	        successions.put(succession.getElementId(), newSuccession);
	    }
		
		if (direction.equals("source")) {	
			successions.get(succession.getElementId()).setSource(nodeAdapter);
			return successions.get(succession.getElementId());
		} else {
			successions.get(succession.getElementId()).setTarget(nodeAdapter);
			return successions.get(succession.getElementId());
		}
	}
	
	public static void resetStatics(){
		successions = new HashMap<>();
	}
}