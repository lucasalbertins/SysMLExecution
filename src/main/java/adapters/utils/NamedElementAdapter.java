package adapters.utils;

import org.omg.sysml.lang.sysml.Element;

import interfaces.utils.INamedElement;

public class NamedElementAdapter implements INamedElement {
	
	private Element namedElement;
	
	public NamedElementAdapter(Element namedElement) {
		this.namedElement = namedElement;
	}

	@Override
	public String getName() {
		return namedElement.getName() != null ? namedElement.getName() : "<no-name>"; // TODO: talver simplficar
	}

	@Override
	public String getDeclaredName() {
		return namedElement.getDeclaredName() != null ? namedElement.getDeclaredName() : "<no-declared-name>";
	}

	@Override
	public String getID() {
		return namedElement.getElementId() != null ? namedElement.getElementId() : "<no-id>";
	}
}