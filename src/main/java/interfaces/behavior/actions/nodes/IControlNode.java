package interfaces.behavior.actions.nodes;

public interface IControlNode extends INode {
	
	public boolean isForkNode();

	public boolean isJoinNode();

	public boolean isDecisionNode();
	
	public boolean isMergeNode();
	
	public boolean isInitialNode();
	
	public boolean isFinalNode();
}
