package adapters.behavior.actions;

import java.util.ArrayList;
import java.util.HashMap;

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
import adapters.utils.FinalNode;
import adapters.utils.InitialNode;
import adapters.utils.NamedElementAdapter;
import adapters.utils.ParameterAdapter;
import interfaces.behavior.actions.IActionDefinition;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.IParameter;

public class ActionDefinitionAdapter extends NamedElementAdapter implements IActionDefinition {

    private INode[] nodes;
    private IParameter[] parameters;
    private IFlow[] flows;
    private HashMap<String,String> owners;

    public ActionDefinitionAdapter(ActionDefinition actionDefinition) {
    	super(actionDefinition);
    	owners = new HashMap<>();
    	
    	ArrayList<INode> nodeList = new ArrayList<INode>();
        ArrayList<IParameter> parameterList = new ArrayList<>();
        ArrayList<IFlow> flowList = new ArrayList<>();
    	
        for (Element element : actionDefinition.getOwnedMember()) {
        	
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

            // ControlNode
            else if (element instanceof ControlNode cn) {
                nodeList.add(new ControlNodeAdapter(cn));
            }

            // FlowUsage
            else if (element instanceof FlowUsage fu) {
                flowList.add(new FlowUsageAdapter(fu));
            }

            // ActionUsage (exceto TransitionUsage)
            else if (element instanceof ActionUsage au && !(element instanceof TransitionUsage)) {
                nodeList.add(new ActionUsageAdapter(au));
                
                
                for (Feature f : au.getOwnedFeature()) {
                   if (f.getDirection() != null && f.getOwner() instanceof ActionUsage owner) {
                        owners.put(new ParameterAdapter(f).getID(), owner.getElementId());
                    }
                }
                
                
            }
            
            // Parameters da ActionDefinition
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
         
    	this.nodes = nodeList.toArray(new INode[0]);
        this.parameters = parameterList.toArray(new IParameter[0]);
        this.flows = flowList.toArray(new IFlow[0]);
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
    public IFlow[] getFlows() {
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