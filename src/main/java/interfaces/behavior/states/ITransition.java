package interfaces.behavior.states;

// OUTDATED
public interface ITransition {
	public String getSourceName();
	
    public String getTargetName();
    
    public IGuard getGuard();
    
    public ITrigger getTrigger();
    
    public IEffect getEffect();
}
