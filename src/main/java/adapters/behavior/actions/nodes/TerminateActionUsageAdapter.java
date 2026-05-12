package adapters.behavior.actions.nodes;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureChainExpression;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;
import org.omg.sysml.lang.sysml.ReferenceUsage;
import org.omg.sysml.lang.sysml.TerminateActionUsage;

import adapters.utils.NamedElementAdapter;
import interfaces.utils.INamedElement;

public class TerminateActionUsageAdapter extends NodeAdapter {
	
	private INamedElement featureChainExpression;
    private INamedElement featureReferenceExpression;
	private Expression expression;

	public TerminateActionUsageAdapter(TerminateActionUsage tau) {
		super(tau);
		
		this.expression = tau.getTerminatedOccurrenceArgument();
		System.out.println(expression);
		for (Element elem : tau.getOwnedMember()) {
			if (elem instanceof ReferenceUsage ru) {
				for (Element elt : ru.getOwnedMember()) {
					if (elt instanceof FeatureChainExpression fce) {
						if (fce.getDeclaredName() != null) {
							this.featureChainExpression = new NamedElementAdapter(fce);
						}
					}
					if (elt instanceof FeatureReferenceExpression fre) {
						if (fre.getDeclaredName() != null) {
							this.featureReferenceExpression = new NamedElementAdapter(fre);
						}
					}
				}
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
