package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.FlowUsage;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;
import org.omg.sysml.lang.sysml.TransitionUsage;

import adapters.utils.AdapterUtils;
import adapters.utils.NamedElementAdapter;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.behavior.actions.nodes.INode;

public class NodeAdapter extends NamedElementAdapter implements INode {

    protected Element nodeElement; // The node itself.
    protected List<ISuccession> incomings;
    protected List<ISuccession> outgoings;
    
    // New lists to support FlowUsage.
    protected List<IFlow> incomingFlows;
    protected List<IFlow> outgoingFlows;

    public NodeAdapter(Element nodeElement) {
        super(nodeElement);
        this.nodeElement = nodeElement;

        Namespace containerNamespace = (Namespace) nodeElement.getOwner();
        ArrayList<ISuccession> incomingList = new ArrayList<>();
        ArrayList<ISuccession> outgoingList = new ArrayList<>();
        ArrayList<IFlow> incomingFlowList = new ArrayList<>();
        ArrayList<IFlow> outgoingFlowList = new ArrayList<>();
        
        for (Element elem : containerNamespace.getOwnedMember()) {
            // Case 1: Direct SuccessionAsUsage
            if (elem instanceof SuccessionAsUsage su) {
                if (nodeElement.getElementId() != null) {
                    for (Element tgt : su.getTarget()) {
                        if (nodeElement.getElementId().equals(tgt.getElementId())) {
                            incomingList.add(AdapterUtils.setSuccession(su, "target", this));
                        }
                    }
                    for (Element src : su.getSource()) {
                        if (nodeElement.getElementId().equals(src.getElementId())) {
                            outgoingList.add(AdapterUtils.setSuccession(su, "source", this));
                        }
                    }
                }
            }
            // Case 2: SuccessionAsUsage inside a TransitionUsage
            if (elem instanceof TransitionUsage tu) {
                if (nodeElement.getElementId() != null) {
                    for (Element sub : tu.getOwnedMember()) {
                        if (!(sub instanceof SuccessionAsUsage su)) continue;

                        for (Element tgt : su.getTarget()) {
                            if (nodeElement.getElementId().equals(tgt.getElementId())) {
                                incomingList.add(AdapterUtils.setSuccession(su, "target", this));
                            }
                        }
                        for (Element src : su.getSource()) {
                            if (nodeElement.getElementId().equals(src.getElementId())) {
                                outgoingList.add(AdapterUtils.setSuccession(su, "source", this));
                            }
                        }
                    }
                }
            }
            // Case 3: FlowUsage (Data/Object Flow)
            if (elem instanceof FlowUsage fu) {
                if (nodeElement.getElementId() != null) {
                    FlowUsageAdapter flowAdapter = new FlowUsageAdapter(fu);
                    
                    // Checks if this node is the target of the flow.
                    IFlowEnd targetEnd = flowAdapter.getTarget();
                    if (targetEnd != null && targetEnd.getReferencedFeature() != null) {
                        if (nodeElement.getElementId().equals(targetEnd.getReferencedFeature().getID())) {
                            incomingFlowList.add(flowAdapter);
                        }
                    }
                    // Checks if this node is the source of the flow.
                    IFlowEnd sourceEnd = flowAdapter.getSource();
                    if (sourceEnd != null && sourceEnd.getReferencedFeature() != null) {
                        if (nodeElement.getElementId().equals(sourceEnd.getReferencedFeature().getID())) {
                            outgoingFlowList.add(flowAdapter);
                        }
                    }
                }
            }
        }
        this.incomings = incomingList;
        this.outgoings = outgoingList;
        this.incomingFlows = incomingFlowList;
        this.outgoingFlows = outgoingFlowList;
    }

    @Override
    public List<ISuccession> getIncomings() {
        return incomings;
    }

    @Override
    public List<ISuccession> getOutgoings() {
        return outgoings;
    }

    @Override
    public List<IFlow> getIncomingFlows() {
        return incomingFlows;
    }

    @Override
    public List<IFlow> getOutgoingFlows() {
        return outgoingFlows;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeAdapter that = (NodeAdapter) o;
        return this.getID().equals(that.getID());
    }

    @Override
    public Element getElement() {
        return nodeElement;
    }
}
