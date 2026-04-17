package interfaces.behavior.actions.nodes;

import interfaces.utils.INamedElement;

public interface IFlowEnd extends INamedElement {

	// Possibility 1: ReferencedFeature
	public INamedElement getReferencedFeature();
	
    // Possibility 2: ChainingFeature inside a ReferencedFeature.
	public INamedElement[] getChainingFeatures();
	
	// ReferenceUsage
	public INamedElement getReferenceUsage();
}
