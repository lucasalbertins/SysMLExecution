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
        // 1. Se não houver expressão de guarda, geralmente assume-se transição incondicional
        if (transitionUsage.getGuardExpression() == null || transitionUsage.getGuardExpression().isEmpty()) {
            return true; 
        }
        // 2. Extrai a Expression da guarda
        Expression guardExpr = transitionUsage.getGuardExpression().get(0);

        // 3. Usa a API nativa do Pilot Implementation para avaliar a expressão dado o contexto
        EList<Element> evaluationResult = EvaluationUtil.evaluate(guardExpr, context);

        // 4. Analisa o retorno. O SysML v2 retorna listas (mesmo para escalares)
        if (evaluationResult != null && !evaluationResult.isEmpty()) {
            Element resultElement = evaluationResult.get(0);
            
            // O resultado final de uma condição lógica deve ser um LiteralBoolean
            if (resultElement instanceof LiteralBoolean literalBool) {
                return literalBool.isValue();
            }
        }
        // Retorna falso se a avaliação falhar ou não retornar um booleano
        return false; 
    }
	
	public Element getSource() {
		return transitionUsage.getSource();
	}
	
	public Element getTarget() {
		return transitionUsage.getTarget();
	}
}
