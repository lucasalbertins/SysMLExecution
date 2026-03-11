package interfaces.behavior.states;

import org.omg.sysml.lang.sysml.ActionUsage;

public interface IEffect {
	public String getEffectType();
	
	public ActionUsage getAction();
	
}
