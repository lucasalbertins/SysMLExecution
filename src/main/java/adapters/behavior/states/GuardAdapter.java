package adapters.behavior.states;

import org.omg.sysml.lang.sysml.Expression;
import interfaces.behavior.states.IGuard;

public class GuardAdapter implements IGuard {
    private Expression expression;

    public GuardAdapter(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }
    
    /*
	@Override
	public String getCondition() {
		if (expression instanceof OperatorExpression) {
			OperatorExpression opExpr = (OperatorExpression) guardExpression;
			return opExpr.getOperator();
		}
		return "Unknown Condition";
	}
	
	@Override
	public String toString() {
		return "Guard Condition: " + getCondition();
	}
    */
}