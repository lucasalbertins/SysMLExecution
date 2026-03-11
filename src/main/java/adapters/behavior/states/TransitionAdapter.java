package adapters.behavior.states;

import org.eclipse.emf.common.util.EList;
import org.omg.sysml.lang.sysml.AcceptActionUsage;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.TransitionUsage;
import org.omg.sysml.lang.sysml.TriggerInvocationExpression;

import interfaces.behavior.states.IEffect;
import interfaces.behavior.states.IGuard;
import interfaces.behavior.states.ITransition;
import interfaces.behavior.states.ITrigger;

public class TransitionAdapter implements ITransition {
    private final TransitionUsage transition;

    public TransitionAdapter(TransitionUsage transition) {
        this.transition = transition;
    }

    @Override
    public String getSourceName() {
        return transition.getSource().getName();
    }

    @Override
    public String getTargetName() {
        return transition.getTarget().getName();
    }

    @Override
    public IGuard getGuard() {
        EList<Expression> guards = transition.getGuardExpression();
        Expression guardExpr = guards.isEmpty() ? null : guards.get(0);
        return guardExpr != null ? new GuardAdapter(guardExpr) : null;
    }

    @Override
    public ITrigger getTrigger() {
        // Aqui pegamos a AcceptActionUsage que é o “trigger” do transition
        EList<AcceptActionUsage> acceptActions = transition.getTriggerAction();
        AcceptActionUsage acc = acceptActions.isEmpty() ? null : acceptActions.get(0);
        if (acc != null) {
            // dentro do AcceptActionUsage há o TriggerInvocationExpression
            Expression payload = acc.getPayloadArgument();
            if (payload instanceof TriggerInvocationExpression) {
                return new TriggerAdapter((TriggerInvocationExpression) payload);
            }
        }
        return null;
    }

    @Override
    public IEffect getEffect() {
        EList<ActionUsage> effects = transition.getEffectAction();
        ActionUsage eff = effects.isEmpty() ? null : effects.get(0);
        return eff != null ? new EffectAdapter(eff) : null;
    }

    @Override
    public String toString() {
        return String.format(
            "Transition from %s to %s\n  Guard: %s\n  Trigger: %s\n  Effect: %s",
            getSourceName(),
            getTargetName(),
            getGuard()   != null ? getGuard()   : "None",
            getTrigger() != null ? getTrigger() : "None",
            getEffect()  != null ? getEffect()  : "None"
        );
    }
}
