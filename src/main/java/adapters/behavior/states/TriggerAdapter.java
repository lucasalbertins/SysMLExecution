package adapters.behavior.states;

import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;
import org.omg.sysml.lang.sysml.TriggerInvocationExpression;

import interfaces.behavior.states.ITrigger;

// OUTDATED
public class TriggerAdapter implements ITrigger {
    private final TriggerInvocationExpression trigger;

    public TriggerAdapter(TriggerInvocationExpression trigger) {
        this.trigger = trigger;
    }

    @Override
    public String getTriggerType() {
        return trigger.getKind().getName();
    }

    /** devolve a expressão bruta, se você precisar navegar no AST */
    
    @Override
    public Expression getTriggerArgument() {
        return trigger.getOperand().isEmpty() ? null : trigger.getOperand().get(0);
    }

    /** helper que retorna só o nome (p.ex. "s") */
    @Override
    public String getArgumentName() {
        Expression arg = getTriggerArgument();
        if (arg instanceof FeatureReferenceExpression) {
            return ((FeatureReferenceExpression)arg).getReferent().getName();
        }
        return arg != null ? arg.toString() : null;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", getTriggerType(), getArgumentName());
    }
}
