package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FlowEnd;
import org.omg.sysml.lang.sysml.ReferenceUsage;

import adapters.utils.NamedElementAdapter;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.utils.INamedElement;

public class FlowEndAdapter extends NamedElementAdapter implements IFlowEnd {

    private INamedElement referencedFeature;
    private INamedElement chainingFeature[];
    private INamedElement referenceUsage;

    public FlowEndAdapter(FlowEnd fe) {
        super(fe);
        // Navigates the features of a FlowEnd (separated into ReferenceSubsetting and ReferenceUsage).
        List<INamedElement> chainingFeatureList = new ArrayList<>();
        
        Feature referenced = fe.getOwnedReferenceSubsetting().getReferencedFeature();
        // Possibility 1: ReferencedFeature
        if (referenced.getDeclaredName() != null) {
            this.referencedFeature = new NamedElementAdapter(referenced);
        }
        // Possibility 2: ChainingFeature inside a ReferencedFeature
        for (Feature f : referenced.getChainingFeature()) {
            chainingFeatureList.add(new NamedElementAdapter(f));
        }
        // ReferenceUsage
        for (Feature f : fe.getOwnedFeature()) {
            if (f instanceof ReferenceUsage) {
                this.referenceUsage = new NamedElementAdapter(f);
            }
        }
        this.chainingFeature = chainingFeatureList.toArray(new INamedElement[0]);
    }
    
    @Override
    public INamedElement getReferencedFeature() {
    	return referencedFeature;
    }
    
    @Override
	public INamedElement[] getChainingFeatures() {
        return chainingFeature;
    }
	
    @Override
	public INamedElement getReferenceUsage() {
        return referenceUsage;
    }
}
