package interfaces.behavior.states;

import org.omg.sysml.lang.sysml.Expression;
//import org.omg.sysml.lang.sysml.TriggerInvocationExpression;


public interface ITrigger {
	public String getTriggerType(); //update later
	
	public Expression getTriggerArgument();
	
	/** helper que retorna só o nome (p.ex. "s") */
	public String getArgumentName();
    
}