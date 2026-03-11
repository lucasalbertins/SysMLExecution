package adapters.behavior.actions;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;

import adapters.behavior.actions.nodes.NodeAdapter;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.states.IGuard;

public class SuccessionAdapter implements ISuccession {

    private SuccessionAsUsage succession;
    
    public SuccessionAdapter(SuccessionAsUsage succession) {
        this.succession = succession;
        
    }

    @Override
    public INode getSource() {
        for (Element src : succession.getSource()) {
            // Nem todo source Ã© Namespace, mas qualquer Element pode ser adaptado
            return new NodeAdapter(src);
        }
        return null;
    }

    @Override
    public INode getTarget() {
        for (Element tgt : succession.getTarget()) {
            return new NodeAdapter(tgt);
        }
        return null;
    }

    @Override
    public IGuard getGuard() {
        return null;
    }

    @Override
    public String getDeclaredName() {
        return succession.getDeclaredName();
    }

    @Override
    public String getName() {
        return succession.getDeclaredName();
    }

	@Override
	public String getID() {
		return succession.getElementId();
	}

	@Override
	public void setSource(INode source) {
		
	}

	@Override
	public void setTarget(INode target) {
	}
}