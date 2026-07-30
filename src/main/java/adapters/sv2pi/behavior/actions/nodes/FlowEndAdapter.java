package adapters.sv2pi.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FlowEnd;
import org.omg.sysml.lang.sysml.ReferenceUsage;

import adapters.sv2pi.utils.NamedElementAdapter;
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
        // ReferencedFeature
        if (referenced != null && (referenced.getDeclaredName() != null || referenced.getName() != null)) {
            this.referencedFeature = new NamedElementAdapter(referenced);
        }
        // ChainingFeature inside a ReferencedFeature
        for (Feature f : referenced.getChainingFeature()) {
            chainingFeatureList.add(new NamedElementAdapter(f));
        }
        
        // ReferenceUsage
        for (Feature f : fe.getOwnedFeature()) {
            if (f instanceof ReferenceUsage) {
                ReferenceUsage ru = (ReferenceUsage) f;
                Feature targetVar = ru;
                
                if (ru.getDeclaredName() == null && ru.getName() == null) {
                    if (!ru.getOwnedSubsetting().isEmpty() && ru.getOwnedSubsetting().get(0).getSubsettedFeature() != null) {
                        targetVar = ru.getOwnedSubsetting().get(0).getSubsettedFeature();
                    } else if (!ru.getOwnedRedefinition().isEmpty() && ru.getOwnedRedefinition().get(0).getRedefinedFeature() != null) {
                        targetVar = ru.getOwnedRedefinition().get(0).getRedefinedFeature();
                    }
                }
                this.referenceUsage = new NamedElementAdapter(targetVar);
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
