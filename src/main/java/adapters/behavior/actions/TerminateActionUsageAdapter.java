package adapters.behavior.actions;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureChainExpression;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;
import org.omg.sysml.lang.sysml.TerminateActionUsage;

import adapters.behavior.actions.nodes.NodeAdapter;
import adapters.utils.NamedElementAdapter;
import interfaces.utils.INamedElement;

public class TerminateActionUsageAdapter extends NodeAdapter {
	
	private INamedElement featureChainExpression;
    private INamedElement featureReferenceExpression;
	private Expression expression;

	public TerminateActionUsageAdapter(TerminateActionUsage tau) {
		super(tau);
		
		this.expression = tau.getTerminatedOccurrenceArgument();
		for (Element elem : tau.getOwnedMember()) {
			if (elem instanceof FeatureChainExpression fce) {
				System.out.println("YAY");
				this.featureChainExpression = new NamedElementAdapter(fce);
			}
			else if (elem instanceof FeatureReferenceExpression fre) {
				System.out.println("YEY");
				this.featureReferenceExpression = new NamedElementAdapter(fre);
			}
		}
	}
	
	public Expression getExpression() {
		return this.expression;
	}
	
	@Override
	public String getDeclaredName() {
		return "terminate";
	}
	
    public INamedElement getfeatureChainExpression() {
    	return this.featureChainExpression;
    }
    
	public INamedElement getfeatureReferenceExpression() {
        return this.featureReferenceExpression;
    }
}
