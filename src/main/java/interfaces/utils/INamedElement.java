package interfaces.utils;

public interface INamedElement extends IElement {
	
	// Inherited by elements that may have a name.
	public String getName();
	
	//String getEffectiveName(); // pode retornar null
	//String getDescription(); // lógica do String describe();
	
	public String getDeclaredName();
}
