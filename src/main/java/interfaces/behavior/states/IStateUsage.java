package interfaces.behavior.states;

import org.omg.sysml.lang.sysml.ActionUsage;

// OUTDATED
public interface IStateUsage {
	
	public String getName();
    
    public ActionUsage getEntry();
    public ActionUsage getDoActivity();
    public ActionUsage getExit();
    
    // TODO: isParallel()
    
    public IStateUsage[] getSubstates();

    public ITransition[] getTransitions();
    
    //IActionUsage[] getAcceptActions();
}