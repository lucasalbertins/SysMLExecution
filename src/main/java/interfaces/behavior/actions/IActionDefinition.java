package interfaces.behavior.actions;

import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.INamedElement;
import interfaces.utils.IParameter;

public interface IActionDefinition extends INamedElement {

	public INode[] getNodes();

    // Returns the names of the action's input and output parameters.
	public IParameter[] getParameters();
    
	// parameter - getDirection
    // - getInputs
    // - getOutpus

    // Returns the names of the internal flows of the action.
	public IFlow[] getFlows(); //Owned

	//public IPartUsage[] getPartUsages();
	//public ISuccession[] getSuccessions();
}
