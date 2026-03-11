package interfaces.behavior.actions.nodes;

import interfaces.utils.INamedElement;

public interface IFlowEnd extends INamedElement {

	// possibilidade 1: ReferencedFeature
	public INamedElement getReferencedFeature();
    // possibilidade 2: ChainingFeature dentro de ReferencedFeature
	public INamedElement[] getChainingFeatures();
	// ReferenceUsage
	public INamedElement getReferenceUsage();

}
