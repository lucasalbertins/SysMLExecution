package interfaces.structures.parts;

import java.util.List;

import interfaces.utils.IFeature;

public interface IPartDefinition {
    // Nome declarado da part definition
	public String getName();
	
    // Retornas as features (subelementos) da definition
	public List<String> getOwnedFeatures();
	
    // PartUsages dentro da PartDefinition (internas)
	public List<String> getOwnedPartUsages();
	
	public String toString();
    
}
