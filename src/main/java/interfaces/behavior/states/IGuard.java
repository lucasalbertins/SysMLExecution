package interfaces.behavior.states;

import org.omg.sysml.lang.sysml.Expression;

import interfaces.structures.expressions.IExpression;

public interface IGuard {

	public IExpression getExpression(); // talvez retornar Expression e, com essa Expression, realizar o evaluate em actions() para avaliar a guarda
    
}