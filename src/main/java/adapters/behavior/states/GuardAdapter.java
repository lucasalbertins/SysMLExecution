package adapters.behavior.states;

import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.OperatorExpression;

import interfaces.behavior.states.IGuard;
import interfaces.structures.expressions.IExpression;

public class GuardAdapter implements IGuard {
    private Expression guardExpression;

    public GuardAdapter(Expression guardExpression) {
        this.guardExpression = guardExpression;
    }

    /*
    @Override
    public String getCondition() {
        if (guardExpression instanceof OperatorExpression) {
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

    @Override
    public Expression getExpression() {
        return guardExpression;
    }

}
