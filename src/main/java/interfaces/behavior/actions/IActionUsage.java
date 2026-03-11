package interfaces.behavior.actions;

import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;

import java.util.List;

import org.omg.sysml.lang.sysml.ActionDefinition;

import interfaces.utils.IParameter;

public interface IActionUsage extends INode {

	public INode[] getNodes();
	
	public IParameter[] getParameters();
	
	public IFlow[] getFlows();
	
	public ActionDefinition getActionDefinition(); // receber quem definiu a Action Usage

	public IParameter[] getInputs();
	
	public IParameter[] getOutputs();
	// public boolean isCallBehaviorAction();

	// public boolean isSendSignalAction();

	// public boolean isAcceptEventAction();
	
}