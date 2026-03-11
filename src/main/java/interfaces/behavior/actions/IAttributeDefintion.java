package interfaces.behavior.actions;

import java.util.List;

import interfaces.utils.IFeature;

public interface IAttributeDefintion extends IFeature{
    // Nome declarado da attribute definition
	public String getName();
	
    // Retornas as features (subelementos) contidas na definition
	public List<IFeature> getOwnedFeatures();
	
}
