package adapters.behavior.states;

import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.AssignmentActionUsage;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;
import org.omg.sysml.lang.sysml.LiteralBoolean;
import org.omg.sysml.lang.sysml.LiteralInteger;
import org.omg.sysml.lang.sysml.LiteralString;
import org.omg.sysml.lang.sysml.PerformActionUsage;
import org.omg.sysml.lang.sysml.SendActionUsage;

import interfaces.behavior.states.IEffect;

public class EffectAdapter implements IEffect {
    private final ActionUsage action;

    public EffectAdapter(ActionUsage action) {
        this.action = action;
    }

    @Override
    public String getEffectType() {
        if (action instanceof PerformActionUsage) {
            return "PerformActionUsage";
        } else if (action instanceof AssignmentActionUsage) {
            return "AssignmentActionUsage";
        } else if (action instanceof SendActionUsage) {
            return "SendActionUsage";
        }
        return "Unknown Effect Type";
    }
    
	@Override
	public ActionUsage getAction() {
		// TO-DO
		return null;
	}

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Effect: ");
        result.append(getEffectType());

        if (action instanceof PerformActionUsage) {
            PerformActionUsage performAction = (PerformActionUsage) action;
            result.append("\n  Perform Action Name: ")
                  .append(performAction.getName() != null ? performAction.getName() : "Unnamed");
        } else if (action instanceof AssignmentActionUsage) {
            AssignmentActionUsage assignAction = (AssignmentActionUsage) action;
            result.append("\n  Assignment Target: ")
                  .append(getExpressionName(assignAction.getTargetArgument()))
                  .append("\n  Assignment Value: ")
                  .append(getExpressionName(assignAction.getValueExpression()));
        } else if (action instanceof SendActionUsage) {
            SendActionUsage sendAction = (SendActionUsage) action;
            result.append("\n  Send Payload: ")
                  .append(getExpressionName(sendAction.getPayloadArgument()))
                  .append("\n  Send Receiver: ")
                  .append(getExpressionName(sendAction.getReceiverArgument()))
                  .append("\n  Send Sender: ")
                  .append(getExpressionName(sendAction.getSenderArgument()));
        }

        return result.toString();
    }

    private String getExpressionName(Expression expression) {
        if (expression instanceof FeatureReferenceExpression) {
            FeatureReferenceExpression refExpr = (FeatureReferenceExpression) expression;
            Feature referent = refExpr.getReferent();
            return referent != null ? referent.getName() : "Unnamed";
        } else if (expression instanceof LiteralString) {
            return ((LiteralString) expression).getValue();
        } else if (expression instanceof LiteralInteger) {
            return String.valueOf(((LiteralInteger) expression).getValue());
        } else if (expression instanceof LiteralBoolean) {
            return String.valueOf(((LiteralBoolean) expression).isValue());
        }
        return "UnknownExpression";
    }
}
