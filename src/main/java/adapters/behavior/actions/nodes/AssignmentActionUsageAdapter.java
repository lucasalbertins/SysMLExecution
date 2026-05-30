package adapters.behavior.actions.nodes;

import org.eclipse.emf.common.util.EList;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.AssignmentActionUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureValue;
import org.omg.sysml.lang.sysml.LiteralExpression;
import org.omg.sysml.lang.sysml.TransitionUsage;
import org.omg.sysml.util.EvaluationUtil;
import org.omg.sysml.util.FeatureUtil;

import interfaces.behavior.actions.IAssignmentActionUsage;

public class AssignmentActionUsageAdapter extends NodeAdapter implements IAssignmentActionUsage {

    private final Feature referent;
    private final Expression valueExpression;

    public AssignmentActionUsageAdapter(AssignmentActionUsage aau) {
        super(aau);
        this.referent = aau.getReferent();
        this.valueExpression = aau.getValueExpression();
    }

    public Feature getReferent() { 
    	return referent; 
    }
    
    public Expression getValueExpression() { 
    	return valueExpression; 
    }

    // Replicated from SuccessionAdapter
    public Element resolveContext() {
        Element current = getElement().getOwner();
        while (current != null) {
            if (current instanceof ActionUsage && !(current instanceof TransitionUsage)) {
                return current;
            }
            current = current.getOwner();
        }
        return getElement().getOwningNamespace();
    }

    @Override
    public void applyAssignment() {
        if (referent == null || valueExpression == null) return;

        try {
            Element context = resolveContext();
            Object newValue = evaluateGeneric(valueExpression, context);
            
            Expression finalExpr = (Expression) EvaluationUtil.elementFor(newValue);

            FeatureValue existing = FeatureUtil.getValuationFor(referent);
            if (existing != null) {
                referent.getOwnedRelationship().remove(existing);
            }
            
            FeatureUtil.addFeatureValueTo(referent, finalExpr);
            System.out.printf("  [Assign] '%s' := %s%n", referent.getDeclaredName(), newValue);

        } catch (Exception e) {
            System.err.printf("  [Assign] ERROR evaluating '%s': %s%n",
                    referent.getDeclaredName(), e.getMessage());
        }
    }

    private Object evaluateGeneric(Expression expr, Element context) {
        EList<Element> result = EvaluationUtil.evaluate(expr, context);

        if (result != null && !result.isEmpty()) {
            Element r = result.get(0);

            if (r instanceof LiteralExpression) {
                return EvaluationUtil.valueOf(r);
            }
        }
        return null;
    }

    @Override
    public String getDeclaredName() {
        String refName = referent != null ? referent.getDeclaredName() : "?";
        return "assign " + refName;
    }
}
