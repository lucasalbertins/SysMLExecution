package interfaces.behavior.actions;

import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;
import interfaces.structures.expressions.IExpression;
import interfaces.utils.IParameter;

import java.util.List;

public interface IActionUsage extends INode {

	public INode[] getNodes();
	public IParameter[] getParameters();
	public IFlow[] getFlows();
	public IActionDefinition getActionDefinition();
	public IParameter[] getInputs();
	public IParameter[] getOutputs();
	public IExpression getArgument();
	
	public boolean isCallAction();
	public boolean isTerminateNode();
	public boolean isAssignmentActionNode();
	public List<IAssignmentActionUsage> getNestedAssignments();
	
	//public boolean isCallBehaviorAction();
	//public boolean isSendSignalAction();
	//public boolean isAcceptEventAction();
}
