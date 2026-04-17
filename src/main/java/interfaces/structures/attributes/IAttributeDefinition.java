package interfaces.structures.attributes;

import java.util.List;

import interfaces.utils.IFeature;

public interface IAttributeDefinition extends IFeature {
	// AttributeDefinition's declared name.
	public String getName();
		
	// Returns the features (sub-elements) contained in the definition.
	public List<IFeature> getOwnedFeatures();
}
