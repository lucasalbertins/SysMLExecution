package interfaces.behavior.actions;

import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.INamedElement;

public interface ISuccession extends INamedElement {

	public INode getTarget();
	public INode getSource();
	public void setSource(INode source);
    public void setTarget(INode target);
    public void setExecutionContext(IActionUsage contextAdapter);
    public boolean evaluateGuard();
	// Maybe generic NamedElement (?)
}
