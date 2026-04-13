package interfaces.behavior.actions.nodes;

import java.util.List;

import org.omg.sysml.lang.sysml.Element;

import interfaces.behavior.actions.ISuccession;
import interfaces.utils.INamedElement;

public interface INode extends INamedElement {

    public List<ISuccession> getIncomings();
    
    public List<ISuccession> getOutgoings();

    // Novos métodos para lidar com fluxos de objetos/dados (Flows)
    public List<IFlow> getIncomingFlows();
    
    public List<IFlow> getOutgoingFlows();
    
    public Element getElement();
    
}