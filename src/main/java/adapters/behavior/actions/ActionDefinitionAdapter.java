package adapters.behavior.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.omg.sysml.lang.sysml.ActionDefinition;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.ControlNode;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FlowUsage;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;
import org.omg.sysml.lang.sysml.TransitionUsage;

import adapters.behavior.actions.nodes.ControlNodeAdapter;
import adapters.behavior.actions.nodes.FlowUsageAdapter;
import adapters.utils.DoneNode;
import adapters.utils.StartNode;
import adapters.utils.NamedElementAdapter;
import adapters.utils.ParameterAdapter;
import interfaces.behavior.actions.IActionDefinition;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.IParameter;

public class ActionDefinitionAdapter extends NamedElementAdapter implements IActionDefinition {

    protected List<INode> nodes;
    protected List<IParameter> parameters;
    protected List<IFlow> flows;
    protected HashMap<String,String> owners;

    // TODO: Analyze the usage of the owners variable.
    public ActionDefinitionAdapter(ActionDefinition actionDefinition) {
    	super(actionDefinition);
    	owners = new HashMap<>();
    	
    	ArrayList<INode> nodeList = new ArrayList<INode>();
        ArrayList<IParameter> parameterList = new ArrayList<>();
        ArrayList<IFlow> flowList = new ArrayList<>();
    	
        for (Element element : actionDefinition.getOwnedMember()) {
            // InitialNode/FinalNode via SuccessionAsUsage.
            if (element instanceof SuccessionAsUsage su) {
                if ("start".equals(su.getSource().getFirst().getDeclaredName())) {
                    StartNode init = new StartNode();
                    init.setDeclaredName("start");
                    init.setOwner(actionDefinition);
                    nodeList.add(new ControlNodeAdapter(init));
                }
                if ("done".equals(su.getTarget().getFirst().getDeclaredName())) {
                    DoneNode fin = new DoneNode();
                    fin.setDeclaredName("done");
                    fin.setOwner(actionDefinition);
                    nodeList.add(new ControlNodeAdapter(fin));
                }
            }
            // ControlNode
            else if (element instanceof ControlNode cn) {
                nodeList.add(new ControlNodeAdapter(cn));
            }
            // FlowUsage
            else if (element instanceof FlowUsage fu) {
                flowList.add(new FlowUsageAdapter(fu));
            }
            // ActionUsage (except TransitionUsage).
            else if (element instanceof ActionUsage au && !(element instanceof TransitionUsage)) {
                nodeList.add(new ActionUsageAdapter(au));
                
                for (Feature f : au.getOwnedFeature()) {
                   if (f.getDirection() != null && f.getOwner() instanceof ActionUsage owner) {
                        owners.put(new ParameterAdapter(f).getID(), owner.getElementId());
                    }
                }
            }
            // ActionDefinition parameters.
            else if (element instanceof Feature f && f.getDirection() != null) {
                parameterList.add(new ParameterAdapter(f));
                owners.put(new ParameterAdapter(f).getID(), actionDefinition.getElementId());
            }
        }
        for (INode node : nodeList) {
        	if (node instanceof IParameter && owners.containsKey(node.getID())) {
        		for (INode possibleOwner : nodeList) {
        			if (possibleOwner.getID().equals(owners.get(node.getID())) &&
        				possibleOwner instanceof ActionUsageAdapter) {
        				IParameter param = (IParameter) node;
        				ActionUsageAdapter action = (ActionUsageAdapter) possibleOwner;
        				param.setActionDefinition((ActionUsage) action);
        			}
        		}
        	}
        }
    	this.nodes = nodeList;
        this.parameters = parameterList;
        this.flows = flowList;
    }
    
    @Override
	public List<INode> getNodes() {
		return this.nodes;
	}

    @Override
    public List<IParameter> getParameters() {
        return this.parameters;
    }

    @Override
    public List<IFlow> getFlows() {
        return this.flows;
    }
    
    public INode getInitial() {
    	for (INode node : this.nodes) {
    		if (node.getDeclaredName().equals("start")) {
    			return node;
    		}
    	}
		return null;
    }
}
