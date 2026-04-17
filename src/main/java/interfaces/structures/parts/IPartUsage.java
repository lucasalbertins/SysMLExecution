package interfaces.structures.parts;

import interfaces.utils.IParameter;

public interface IPartUsage extends IParameter {

	public String getName();
	
    // Specialization name (classifier/type). 
	public String getSpecialization();
    
	public String toString();
}
