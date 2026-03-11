package interfaces.structures.attributes;

import java.util.List;

import interfaces.utils.INamedElement;

public interface IAttributeDefintion extends INamedElement {
    // Nome declarado da attribute definition
	public String getName();
	
    // Retornas as features (subelementos) contidas na definition
	public List<INamedElement> getOwnedFeatures(); // trocou de IFeature
}
