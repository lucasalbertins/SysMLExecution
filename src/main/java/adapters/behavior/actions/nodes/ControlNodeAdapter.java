package adapters.behavior.actions.nodes;

import org.omg.sysml.lang.sysml.DecisionNode;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.ForkNode;
import org.omg.sysml.lang.sysml.JoinNode;
import org.omg.sysml.lang.sysml.MergeNode;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;

import adapters.utils.AdapterUtils;
import adapters.utils.FinalNode;
import adapters.utils.InitialNode;
import interfaces.behavior.actions.nodes.IControlNode;

public class ControlNodeAdapter extends NodeAdapter implements IControlNode {
	
	public ControlNodeAdapter(Element controlNodeElement) {
		super(controlNodeElement);
		if (controlNodeElement instanceof InitialNode) {
			Element owner = controlNodeElement.getOwner();
			for (Element element : owner.getOwnedElement()) {
	        	
	            // Initial / Final via Succession
	            if (element instanceof SuccessionAsUsage su) {

	                if ("start".equals(su.getSource().getFirst().getDeclaredName())) {
	                	SuccessionAsUsage s = ((SuccessionAsUsage)element);
	                	super.outgoings.add(AdapterUtils.setSuccession(su, "source", this));
	                }

	            }

			}
		} else if (controlNodeElement instanceof FinalNode) {
			Element owner = controlNodeElement.getOwner();
			for (Element element : owner.getOwnedElement()) {
	        	
	            // Initial / Final via Succession
	            if (element instanceof SuccessionAsUsage su) {

	                if ("done".equals(su.getTarget().getFirst().getDeclaredName())) {
	                	SuccessionAsUsage s = ((SuccessionAsUsage)element);
	                	super.incomings.add(AdapterUtils.setSuccession(su, "target", this));
	                }

	            }

			}
		}
	}

	@Override
	public boolean isDecisionNode() {
		return nodeElement instanceof DecisionNode;
	}
	
	@Override
	public boolean isForkNode() {
		return nodeElement instanceof ForkNode;
	}

	@Override
	public boolean isJoinNode() {
		return nodeElement instanceof JoinNode;
	}

	@Override
	public boolean isMergeNode() {
		return nodeElement instanceof MergeNode;
	}

	@Override
	public boolean isInitialNode() {
		return nodeElement instanceof InitialNode;
	}

	@Override
	public boolean isFinalNode() {
		return nodeElement instanceof FinalNode;
	}
}
