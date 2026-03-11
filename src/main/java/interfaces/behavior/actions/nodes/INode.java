package interfaces.behavior.actions.nodes;

import java.util.List;

import org.omg.sysml.lang.sysml.Element;

import interfaces.behavior.actions.ISuccession;
import interfaces.utils.INamedElement;

public interface INode extends INamedElement {

	public List<ISuccession> getIncomings();
	
	public List<ISuccession> getOutgoings();
	
	public Element getElement();
	
}
