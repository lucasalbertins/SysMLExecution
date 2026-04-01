package adapters.behavior.actions;

import org.omg.sysml.lang.sysml.DecisionNode;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.ForkNode;
import org.omg.sysml.lang.sysml.JoinNode;
import org.omg.sysml.lang.sysml.MergeNode;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;

import adapters.behavior.actions.nodes.ControlNodeAdapter;
import adapters.behavior.actions.nodes.NodeAdapter;
import adapters.utils.FinalNode;
import adapters.utils.InitialNode;
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
        	// Se for um nó de controle do SysML ou nossos nós sintéticos
            if (src instanceof DecisionNode || 
                src instanceof MergeNode || 
                src instanceof ForkNode || 
                src instanceof JoinNode ||
                src instanceof InitialNode || 
                src instanceof FinalNode) {
                
                return new ControlNodeAdapter(src);
            }
            
            // Padrão: Se for um ActionUsage genérico ou não mapeado
            return new NodeAdapter(src);
        }
        return null;
    }

    @Override
    public INode getTarget() {
        for (Element tgt : succession.getTarget()) {
        	// Se for um nó de controle ou nossos nós sintéticos
            if (tgt instanceof DecisionNode || 
                tgt instanceof MergeNode || 
                tgt instanceof ForkNode || 
                tgt instanceof JoinNode ||
                tgt instanceof InitialNode || 
                tgt instanceof FinalNode) {
                
                return new ControlNodeAdapter(tgt);
            }
            
            // Padrão: Se for um ActionUsage genérico ou não mapeado
            return new NodeAdapter(tgt);
        }
        return null;
    }

    @Override
    public IGuard getGuard() {
        for (Element guard : succession.getMember()) {
            if (guard instanceof Expression e) { 
                return (IGuard) e;
            }
        } 
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