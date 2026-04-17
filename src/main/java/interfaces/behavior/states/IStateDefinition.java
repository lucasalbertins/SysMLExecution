package interfaces.behavior.states;

import interfaces.utils.INamedElement;

// OUTDATED
public interface IStateDefinition extends INamedElement {
    
	public ITransition[] getTransitions();
    
	public IStateUsage[] getStates();
}
