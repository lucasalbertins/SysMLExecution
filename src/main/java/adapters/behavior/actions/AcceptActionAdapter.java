package adapters.behavior.actions;

//import org.eclipse.emf.common.util.EList;
import org.omg.sysml.lang.sysml.AcceptActionUsage;

// OUTDATED
public class AcceptActionAdapter /* implements IAcceptAction*/ {
    private final AcceptActionUsage action;

    public AcceptActionAdapter(AcceptActionUsage action) {
        this.action = action;
    }
    
    /*
    @Override
    public String getName() {
        return action.getDeclaredName();
    }

    @Override
    public String getPayloadName() {
        // procura a feature “payload” dentro de ownedFeature
        for (Feature f : action.getOwnedFeature()) {
            if ("payload".equals(f.getName()) && f instanceof ReferenceUsage) {
                ReferenceUsage ref = (ReferenceUsage) f;
                if (!ref.getDefinition().isEmpty()) {
                    return ref.getDefinition().get(0).getName();
                }
            }
        }
        return null;
    }

    @Override
    public String getReceiverName() {
        // tenta ler receiverArgument
        var recv = action.getReceiverArgument();
        if (recv instanceof FeatureReferenceExpression) {
            return ((FeatureReferenceExpression) recv).getReferent().getName();
        }
        return null;
    }
    */
}
