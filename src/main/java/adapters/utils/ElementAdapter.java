package adapters.utils;

import org.omg.sysml.lang.sysml.Element;

import interfaces.utils.IElement;

public class ElementAdapter implements IElement {
	
	Element element;
	
	public ElementAdapter(Element element) {
		this.element = element;
	}
	
	// Extracts the ID of the element.
	@Override
	public String getID() {
	    try {
	        return element.getElementId() != null
	                ? element.getElementId()
	                : "<no-id>";
	    } catch (Exception e) {
	        return "<error-getting-id>";
	    }
	}
}
