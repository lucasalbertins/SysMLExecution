package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import adapters.behavior.actions.SuccessionAdapter;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IControlNode;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.actions.nodes.INodeCommand;

import gamine.domain.SysMLV2Configuration;

public class ActionNodeCommand implements INodeCommand {
    
    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
    	// 1. Creates editable copies of the current successions and flows.
        List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
        List<IFlow> nextFlows = new ArrayList<>(configuration.flows);
        
        // 2. Handles the copy of successions (Control Flow).
        removeIncomings(node, nextSuccessions);
        addOutgoings(node, nextSuccessions);
        
        // 3. Handles the copy of flows (Data/Object Flow).
        removeIncomingFlows(node, nextFlows);
        addOutgoingFlows(node, nextFlows);
        
        // 4. Returns the new state using the full constructor.
        return List.of(new SysMLV2Configuration(nextSuccessions, nextFlows));
    }

    protected void removeIncomings(INode node, List<ISuccession> nextSuccessions) {
        if (node.getIncomings() == null) return;
        
        if (node.getIncomings().size() > 1 && !(node instanceof IControlNode)) {
        	System.err.printf("  [WARNING] Implicit JoinNode detected in Action '%s'. " +
                    "(%d incoming edges) \n  The use of an explicit JoinNode is recommended.%n", 
                    node.getDeclaredName(), node.getIncomings().size());
        }
        
        /*
        throw new IllegalStateException(
            String.format("SysML Topology Violation: Action '%s' has multiple entries (%d). " +
                          "You must use an explicit 'JoinNode' to synchronize control flows.", 
                          nodeName, node.getIncomings().size())
        );
        */
        
        for (ISuccession incoming : node.getIncomings()) {
        	// Removes EXACTLY 1 succession per incoming edge.
            for (int i = 0; i < nextSuccessions.size(); i++) {
                if (nextSuccessions.get(i).getID().equals(incoming.getID())) {
                    System.out.println("  [-] Succession consumed: " + nextSuccessions.get(i).getID());
                    nextSuccessions.remove(i);
                    break; // Interrupts to avoid consuming duplicate successions on the same edge.
                }
            }
        }
    }

    protected void addOutgoings(INode node, List<ISuccession> nextSuccessions) {
        if (node.getOutgoings() == null) return;
        
        for (ISuccession outgoing : node.getOutgoings()) {
            nextSuccessions.add((SuccessionAdapter) outgoing);
            System.out.println("  [+] Succession produced: " + outgoing.getID());
        }
    }

    // --- FLOW METHODS ---
    protected void removeIncomingFlows(INode node, List<IFlow> nextFlows) {
        if (node.getIncomingFlows() == null) return;
        
        for (IFlow incomingFlow : node.getIncomingFlows()) {
            for (int i = 0; i < nextFlows.size(); i++) {
                if (nextFlows.get(i).getID().equals(incomingFlow.getID())) {
                    String payloadName = incomingFlow.getPayload() != null ? incomingFlow.getPayload().getDeclaredName() : "<no-payload>";
                    System.out.printf("  [-] Flow consumed %s: %s [Payload: %s]%n", 
                    		nextFlows.get(i).getDeclaredName(), 
                    		nextFlows.get(i).getID(), 
                            payloadName);
                    nextFlows.remove(i);
                    break;
                }
            }
        }
    }

    protected void addOutgoingFlows(INode node, List<IFlow> nextFlows) {
        if (node.getOutgoingFlows() == null) return;
        
        for (IFlow outgoingFlow : node.getOutgoingFlows()) {
            nextFlows.add(outgoingFlow);
            String payloadName = outgoingFlow.getPayload() != null ? outgoingFlow.getPayload().getDeclaredName() : "<no-payload>";
            System.out.printf("  [+] Flow produced %s: %s [Payload: %s]%n", 
            		outgoingFlow.getDeclaredName(), 
            		outgoingFlow.getID(), 
                    payloadName);
        }
    }
}
