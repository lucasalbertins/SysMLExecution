package interfaces.behavior.actions;

import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;
import interfaces.structures.expressions.IExpression;

import java.util.List;

import org.omg.sysml.lang.sysml.ActionDefinition;

import interfaces.utils.IParameter;

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
	
	//public boolean isCallBehaviorAction();
	//public boolean isSendSignalAction();
	//public boolean isAcceptEventAction();
}
