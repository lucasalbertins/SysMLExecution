package interfaces.behavior.actions;

import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.INamedElement;

public interface ISuccession extends INamedElement {

	public INode getTarget();

	public INode getSource();
	
	void setSource(INode source);
	
    void setTarget(INode target);
	// Maybe generic NamedElement (?)
}
