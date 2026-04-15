package adapters.behavior.actions;

import org.eclipse.emf.common.util.EList;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.DecisionNode;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.ForkNode;
import org.omg.sysml.lang.sysml.JoinNode;
import org.omg.sysml.lang.sysml.LiteralBoolean;
import org.omg.sysml.lang.sysml.MergeNode;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;
import org.omg.sysml.lang.sysml.TransitionUsage;
import org.omg.sysml.util.EvaluationUtil;

import adapters.behavior.actions.nodes.ControlNodeAdapter;
import adapters.behavior.actions.nodes.NodeAdapter;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class SuccessionAdapter implements ISuccession {

    private SuccessionAsUsage succession;
    private Element executionContext; // O contexto real da simulação
    
    public SuccessionAdapter(SuccessionAsUsage succession) {
        this.succession = succession;
    }

    // Injeção de dependência para receber o contexto dinâmico do simulador
    public void setExecutionContext(Element executionContext) {
        this.executionContext = executionContext;
    }

    @Override
    public INode getSource() {
        for (Element src : succession.getSource()) {
            if (src instanceof DecisionNode || 
                src instanceof MergeNode || 
                src instanceof ForkNode || 
                src instanceof JoinNode) {
                
                return new ControlNodeAdapter(src);
            }
            if (src.getDeclaredName().equals("start") ||
                src.getDeclaredName().equals("done")) {
                return new ControlNodeAdapter(src);
            }
            
            return new NodeAdapter(src);
        }
        return null;
    }

    @Override
    public INode getTarget() {
        for (Element tgt : succession.getTarget()) {
            if (tgt instanceof DecisionNode || 
                tgt instanceof MergeNode || 
                tgt instanceof ForkNode || 
                tgt instanceof JoinNode) {
                
                return new ControlNodeAdapter(tgt);
            }
            if (tgt.getDeclaredName().equals("start") ||
                tgt.getDeclaredName().equals("done")) {
                return new ControlNodeAdapter(tgt);
            }
            
            return new NodeAdapter(tgt);
        }
        return null;
    }

    // Avalia a guarda de forma autônoma
    public boolean evaluateGuard() {
        Expression guardExpr = extractGuardExpression();

        if (guardExpr == null) {
            return true; // Transição sem guarda
        }

        Element contextToUse = (this.executionContext != null) ? this.executionContext : resolveContext();
        EList<Element> result = EvaluationUtil.evaluate(guardExpr, contextToUse);

        if (result != null && !result.isEmpty()) {
            Element resElement = result.get(0);
            if (resElement instanceof LiteralBoolean lb) {
                return lb.isValue();
            }
        }

        return false;
    }
    
    private Expression extractGuardExpression() {
        for (Element member : succession.getMember()) {
            if (member instanceof Expression e) return e;
        } 
        if (succession.getOwner() instanceof TransitionUsage tu) {
            for (Element member : tu.getMember()) {
                if (member instanceof Expression e) return e;
            }
        }
        return null;
   }

    public Element resolveContext() {
        Element current = succession.getOwner();
        
        while (current != null) {
            if (current instanceof ActionUsage && !(current instanceof TransitionUsage)) {
                return current;
            }
            current = current.getOwner();
        }
        
        return succession.getOwningNamespace(); 
    }
    

    public void setExecutionContext(ActionUsageAdapter contextAdapter) {
        if (contextAdapter != null) {
            // Usa o método herdado do NodeAdapter para pegar o elemento físico
            this.executionContext = contextAdapter.getElement();
        }
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
    public void setSource(INode source) {}

    @Override
    public void setTarget(INode target) {}
}