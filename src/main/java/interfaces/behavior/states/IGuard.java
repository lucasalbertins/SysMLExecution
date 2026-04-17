package interfaces.behavior.states;

import org.omg.sysml.lang.sysml.Expression;

// OUTDATED
public interface IGuard {

	public Expression getExpression(); 
	// Perhaps return an Expression and, using that Expression, 
	// perform an evaluate in actions() to assess the guard.
}
