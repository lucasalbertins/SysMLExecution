package interfaces.utils;

public interface IFeature extends INamedElement {

	public boolean isReadOnly();
    
	public boolean isOrdered();
    
	public boolean isUnique();
    
	public boolean isComposite();
	
	//IType getType();
    //IElement getOwningType();
	//public FeatureDirectionKind getDirection();
    
    // TODO: Analyze multiplicity (?)
}
