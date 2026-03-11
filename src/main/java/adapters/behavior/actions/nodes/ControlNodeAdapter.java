package adapters.behavior.actions.nodes;

import org.omg.sysml.lang.sysml.DecisionNode;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.ForkNode;
import org.omg.sysml.lang.sysml.JoinNode;
import org.omg.sysml.lang.sysml.MergeNode;
import org.omg.sysml.lang.sysml.Namespace;

import interfaces.behavior.actions.nodes.IControlNode;

public class ControlNodeAdapter extends NodeAdapter implements IControlNode {
	
	private boolean isForkNode;
	private boolean isJoinNode;
	private boolean isDecisionNode;
	private boolean isMergeNode;
	
	public ControlNodeAdapter(Element controlNodeElement) {
		super(controlNodeElement);
		
		this.isDecisionNode = controlNodeElement instanceof DecisionNode;
        this.isForkNode = controlNodeElement instanceof ForkNode;
        this.isJoinNode = controlNodeElement instanceof JoinNode;
        this.isMergeNode = controlNodeElement instanceof MergeNode;
	}

	@Override
	public boolean isDecisionNode() {
		return isDecisionNode;
	}
	
	@Override
	public boolean isForkNode() {
		return isForkNode;
	}

	@Override
	public boolean isJoinNode() {
		return isJoinNode;
	}

	@Override
	public boolean isMergeNode() {
		return isMergeNode;
	}
}
