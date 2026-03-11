package interfaces.structures.parts;

import interfaces.utils.IParameter;

public interface IPartUsage extends IParameter {

	public String getName();
    // Nome da especialização (classifier/type) 
	
	public String getSpecialization();
    
	public String toString();
    
}