package interfaces.behavior.actions;

import interfaces.behavior.actions.nodes.INode;

public interface IAssignmentActionUsage extends INode {
	
	public void applyAssignment();
	public String getTargetName();
	public String getTargetID();
	public Object getCurrentValue();
}