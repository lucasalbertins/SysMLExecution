package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;
import org.omg.sysml.lang.sysml.TransitionUsage;

import adapters.utils.AdapterUtils;
import adapters.utils.NamedElementAdapter;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class NodeAdapter extends NamedElementAdapter implements INode {

    protected Element nodeElement; // o nó em si
    protected List<ISuccession> incomings;
    protected List<ISuccession> outgoings;

    public NodeAdapter(Element nodeElement) {
        super(nodeElement);
        this.nodeElement = nodeElement;

        Namespace containerNamespace = (Namespace) nodeElement.getOwner();
        ArrayList<ISuccession> incomingList = new ArrayList<>();
        ArrayList<ISuccession> outgoingList = new ArrayList<>();
        
        for (Element elem : containerNamespace.getOwnedMember()) {

            // Caso 1: SuccessionAsUsage direto
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

            // Caso 2: SuccessionAsUsage dentro de TransitionUsage
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
        }

        this.incomings = incomingList;
        this.outgoings = outgoingList;
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