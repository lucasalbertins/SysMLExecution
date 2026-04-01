package adapters.behavior.actions.nodes;

import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FlowEnd;
import org.omg.sysml.lang.sysml.FlowUsage;

import adapters.utils.NamedElementAdapter;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.utils.INamedElement;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.PayloadFeature;

public class FlowUsageAdapter extends NamedElementAdapter implements IFlow {
	
	private IFlowEnd source;
	private IFlowEnd target;
	private INamedElement payload; // PayloadFeature

	public FlowUsageAdapter(FlowUsage flow) {
        super(flow);
        // navega features de uma FlowUsage (separado em PayloadFeature e FlowEnd)
        for (Feature feat : flow.getOwnedFeature()) {
        	// extrai o tipo do payload de cada FlowUsage
            if (feat instanceof PayloadFeature pf) {
                Element payloadType = pf.getType().get(0);
                this.payload = new NamedElementAdapter(payloadType);
            }
            // extrai o source e target de cada FlowEnd
            if (feat instanceof FlowEnd fe) {
                if ("source".equals(fe.getFeatureTarget().getName())) {
                    this.source = new FlowEndAdapter(fe);
                } else {
                    this.target = new FlowEndAdapter(fe);
                }
            }
        }
    }

	@Override
	public IFlowEnd getSource() {
		return source;
	}

	@Override
	public IFlowEnd getTarget() {
		return target;
	}

	@Override
	public INamedElement getPayload() {
		return payload;
	}
}
