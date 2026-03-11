package interfaces.behavior.states;

//import interfaces.actions.IActionUsage;

public interface ITransition {
	public String getSourceName();
	
    public String getTargetName();
    
    public IGuard   getGuard();
    
    public ITrigger getTrigger();
    
    public IEffect  getEffect();
    
}