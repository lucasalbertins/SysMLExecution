package interfaces.behavior.actions.nodes;

import interfaces.utils.INamedElement;

public interface IFlow extends INamedElement {

    public IFlowEnd getSource();

    public IFlowEnd getTarget();

    // PayloadFeature
    public INamedElement getPayload();
}
