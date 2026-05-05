package adapters.behavior.actions.nodes;

import org.omg.sysml.lang.sysml.DecisionNode;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.ForkNode;
import org.omg.sysml.lang.sysml.JoinNode;
import org.omg.sysml.lang.sysml.MergeNode;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;
import org.omg.sysml.lang.sysml.TerminateActionUsage;

import adapters.utils.AdapterUtils;
import adapters.utils.FinalNode;
import adapters.utils.InitialNode;
import interfaces.behavior.actions.nodes.IControlNode;

public class ControlNodeAdapter extends NodeAdapter implements IControlNode {
	
	// TODO: Properly connect synthetic nodes (Initial/Final) to natural ones.
	public ControlNodeAdapter(Element controlNodeElement) {
		super(controlNodeElement);
		if (controlNodeElement instanceof InitialNode) {
			Element owner = controlNodeElement.getOwner();
			for (Element element : owner.getOwnedElement()) {
	            // InitialNode via SuccessionAsUsage.
	            if (element instanceof SuccessionAsUsage su) {
	                if ("start".equals(su.getSource().getFirst().getDeclaredName())) {
	                	super.outgoings.add(AdapterUtils.setSuccession(su, "source", this));
	                }
	            }
			}
		} else if (controlNodeElement instanceof FinalNode) {
			Element owner = controlNodeElement.getOwner();
			for (Element element : owner.getOwnedElement()) {
	            // FinalNode via SuccessionAsUsage.
	            if (element instanceof SuccessionAsUsage su) {
	                if ("done".equals(su.getTarget().getFirst().getDeclaredName())) {
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
	public boolean isStartNode() {
		// return nodeElement instanceof InitialNode;
		return "start".equals(nodeElement.getDeclaredName());
	}

	@Override
	public boolean isDoneNode() {
		// return nodeElement instanceof FinalNode;
		return "done".equals(nodeElement.getDeclaredName());
	}

	// TODO: Analyze whether this method should belong to ActionUsageAdapter
	@Override
	public boolean isTerminateNode() {
		return this.nodeElement instanceof TerminateActionUsage;
	}
}
