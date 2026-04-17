package interfaces.behavior.states;

import org.omg.sysml.lang.sysml.Expression;
//import org.omg.sysml.lang.sysml.TriggerInvocationExpression;

// OUTDATED
public interface ITrigger {
	
	public String getTriggerType(); // TODO: Update later.
	
	public Expression getTriggerArgument();
	
	// Helper that only returns the name (p.ex. "s").
	public String getArgumentName();
}
