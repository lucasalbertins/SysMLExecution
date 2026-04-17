package adapters.behavior.actions.nodes;

import org.eclipse.emf.common.util.EList;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.LiteralBoolean;
import org.omg.sysml.lang.sysml.TransitionUsage;
import org.omg.sysml.util.EvaluationUtil;

public class TransitionUsageAdapter extends NodeAdapter {

	public TransitionUsage transitionUsage;
	
	public TransitionUsageAdapter(TransitionUsage transitionUsage) {
		super(transitionUsage);
		this.transitionUsage = transitionUsage;
	}
	
	public boolean evaluateGuard(Element context) {
        // 1. If there isn't a guard expression, unconditional transition is generally assumed.
        if (transitionUsage.getGuardExpression() == null || transitionUsage.getGuardExpression().isEmpty()) {
            return true; 
        }
        // 2. Extracts the expression from the guard.
        Expression guardExpr = transitionUsage.getGuardExpression().get(0);

        // 3. Utilizes the Pilot Implementation's native API to evaluate the expression given the context.
        EList<Element> evaluationResult = EvaluationUtil.evaluate(guardExpr, context);

        // 4. Analyzes the return. SysML v2 retirns lists (even for scalars).
        if (evaluationResult != null && !evaluationResult.isEmpty()) {
            Element resultElement = evaluationResult.get(0);
            
            // The final result of a logical condition must be a LiteralBoolean.
            if (resultElement instanceof LiteralBoolean literalBool) {
                return literalBool.isValue();
            }
        }
        // Returns false if the evaluation fails or does not return a boolean.
        return false; 
    }
	
	public Element getSource() {
		return transitionUsage.getSource();
	}
	
	public Element getTarget() {
		return transitionUsage.getTarget();
	}
}
