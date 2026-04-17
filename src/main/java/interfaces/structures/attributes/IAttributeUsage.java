package interfaces.structures.attributes;

import interfaces.utils.IParameter;

public interface IAttributeUsage extends IParameter {
	
	public String getName();
	
	public String getType();        // Returns the type: Real, Integer, etc.
	
	public String getUnit();        // Ex: SI::kg
	
	public String getValue();       // Ex: "1350"
}
