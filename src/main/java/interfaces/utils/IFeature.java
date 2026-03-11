package interfaces.utils;

public interface IFeature extends INamedElement {
// IType getType();
	
    // IElement getOwningType();
    
	public boolean isReadOnly();
    
	public boolean isOrdered();
    
	public boolean isUnique();
    
	public boolean isComposite();
    
    // Multiplicity?
    
    // Trazer getDirection para cá?
    // FeatureDirectionKind
    
}
