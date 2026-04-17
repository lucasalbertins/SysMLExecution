package interfaces.structures.parts;

import java.util.List;

public interface IPartDefinition {
	
    // PartDefinition's declared name.
	public String getName();
	
    // Returns the features (sub-elements) of the definition.
	public List<String> getOwnedFeatures();
	
    // PartUsages within the PartDefinition (internal).
	public List<String> getOwnedPartUsages();
	
	public String toString();
}
