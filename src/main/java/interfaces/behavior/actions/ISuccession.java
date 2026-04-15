package interfaces.behavior.actions;


import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.states.IGuard;
import interfaces.utils.INamedElement;

public interface ISuccession extends INamedElement {

	public INode getTarget();

	public INode getSource();
	
	void setSource(INode source);
	
    void setTarget(INode target);
	
	// Precisaria de um NamedElement genérico?
}