package interfaces.behavior.states;

import org.omg.sysml.lang.sysml.Expression;

public interface IGuard {

	public Expression getExpression(); // talvez retornar Expression e, com essa Expression, realizar o evaluate em actions() para avaliar a guarda

}