package adapters.behavior.actions;

import java.util.ArrayList;
import java.util.List;

import org.omg.sysml.lang.sysml.ActionDefinition;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.ControlNode;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureDirectionKind;
import org.omg.sysml.lang.sysml.FlowUsage;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;
import org.omg.sysml.lang.sysml.TransitionUsage;

import adapters.behavior.actions.nodes.ControlNodeAdapter;
import adapters.behavior.actions.nodes.FlowUsageAdapter;
import adapters.behavior.actions.nodes.NodeAdapter;
import adapters.utils.FinalNode;
import adapters.utils.InitialNode;
import adapters.utils.ParameterAdapter;
import interfaces.behavior.actions.IActionUsage;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.IParameter;

public class ActionUsageAdapter extends NodeAdapter implements IActionUsage {
	
	private ActionDefinition actionDefinition;
	private IParameter[] parameters;
	private INode[] nodes;
	private IFlow[] flows;

	public ActionUsageAdapter(ActionUsage actionUsage) {
		super(actionUsage);
		
		ArrayList<IParameter> parameterList = new ArrayList<>();
		ArrayList<INode> nodeList = new ArrayList<>();
		ArrayList<IFlow> flowList = new ArrayList<>();
		
		if (actionUsage.getActionDefinition().getFirst() != null) {
			this.actionDefinition = (ActionDefinition) actionUsage.getActionDefinition().getFirst();
		}
		
		for (Element element : actionUsage.getOwnedMember()) {
			
			// Initial / Final via Succession
			if (element instanceof SuccessionAsUsage su) {
				if ("start".equals(su.getSource().getFirst().getDeclaredName())) {
                    InitialNode init = new InitialNode();
                    init.setDeclaredName("start");
                    init.setOwner(actionDefinition);
                    nodeList.add(new ControlNodeAdapter(init));
                }

                if ("done".equals(su.getTarget().getFirst().getDeclaredName())) {
                    FinalNode fin = new FinalNode();
                    fin.setDeclaredName("done");
                    fin.setOwner(actionDefinition);
                    nodeList.add(new ControlNodeAdapter(fin));
                }
			}
			
			// ActionUsage (exceto TransitionUsage)
            else if (element instanceof ActionUsage au && !(element instanceof TransitionUsage)) {
                nodeList.add(new ActionUsageAdapter(au));
                
            } 
			
			// Parameters da ActionUsage
            else if (element instanceof Feature f && f.getDirection() != null) {
                parameterList.add(new ParameterAdapter(f));
            }
			
        	// ControlNode
        	else if (element instanceof ControlNode cn) {
        		nodeList.add(new ControlNodeAdapter(cn));
        	} 
			
        	// FlowUsage
        	else if (element instanceof FlowUsage fu) {
        		nodeList.add(new NodeAdapter(fu));
        		flowList.add(new FlowUsageAdapter(fu));
        	}
        }
		
		this.parameters = parameterList.toArray(new IParameter[0]);
		this.nodes = nodeList.toArray(new INode[0]);
		this.flows = flowList.toArray(new IFlow[0]);
	}
	
	// Método auxiliar para separar inputs de outputs da lista de parâmetros
	private IParameter[] extractByDirection(FeatureDirectionKind... dirs) {
	    List<IParameter> result = new ArrayList<>();

	    for (IParameter p : parameters) {
	        for (FeatureDirectionKind d : dirs) {
	            if (p.getDirection() == d) {
	                result.add(p);
	                break;
	            }
	        }
	    }
	    return result.toArray(new IParameter[0]);
	}
	
	@Override
	public INode[] getNodes() {
		return this.nodes;
	}

	@Override
	public IParameter[] getParameters() {
		return this.parameters;
	}

	@Override
	public ActionDefinition getActionDefinition() {
		return this.actionDefinition;
	}

	@Override
	public IFlow[] getFlows() {
		return this.flows;
	}

	@Override
	public IParameter[] getInputs() {
	    return extractByDirection(FeatureDirectionKind.IN, FeatureDirectionKind.INOUT);
	}

	@Override
	public IParameter[] getOutputs() {
	    return extractByDirection(FeatureDirectionKind.OUT, FeatureDirectionKind.INOUT);
	}

	public boolean isCallAction() {
	    return actionDefinition != null;
	}
}