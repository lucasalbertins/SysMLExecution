package interfaces.behavior.states;

import interfaces.utils.INamedElement;

public interface IStateDefinition extends INamedElement {
    
	public ITransition[] getTransitions();
    
	public IStateUsage[] getStates();
    
}
