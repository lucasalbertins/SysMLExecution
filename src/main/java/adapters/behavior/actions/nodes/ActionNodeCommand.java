package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import adapters.behavior.actions.SuccessionAdapter;
import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IControlNode;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.actions.nodes.INodeCommand;

public class ActionNodeCommand implements INodeCommand {

    @Override
    public boolean isEnabled(INode node, SysMLV2Configuration configuration) {
        if (node == null) return true;

        Set<String> activeSuccIds = configuration.successions.stream()
                .map(ISuccession::getID)
                .collect(Collectors.toSet());

        // Standard Logic (AND): Requires ALL input tokens (Control)
        boolean successionsEnabled = true;
        if (node.getIncomings() != null && !node.getIncomings().isEmpty()) {
            successionsEnabled = node.getIncomings().stream()
                    .map(ISuccession::getID)
                    .allMatch(activeSuccIds::contains);
        }

        // Standard Logic (AND): Requires ALL input tokens (Data/Object)
        boolean flowsEnabled = true;
        if (node.getIncomingFlows() != null && !node.getIncomingFlows().isEmpty()) {
             Set<String> activeFlowIds = configuration.flows.stream().map(IFlow::getID).collect(Collectors.toSet());
             flowsEnabled = node.getIncomingFlows().stream().map(IFlow::getID).allMatch(activeFlowIds::contains);
        }

        return successionsEnabled && flowsEnabled;
    }

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
        List<IFlow> nextFlows = new ArrayList<>(configuration.flows);
        
        removeIncomings(node, nextSuccessions);
        addOutgoings(node, nextSuccessions);
        
        removeIncomingFlows(node, nextFlows);
        addOutgoingFlows(node, nextFlows);
        
        return List.of(new SysMLV2Configuration(nextSuccessions, nextFlows));
    }

    protected void removeIncomings(INode node, List<ISuccession> nextSuccessions) {
        if (node.getIncomings() == null || node.getIncomings().isEmpty()) {
            // Fallback for generic nodes poorly mapped by the parser.
            for (int i = 0; i < nextSuccessions.size(); i++) {
                INode target = nextSuccessions.get(i).getTarget();
                if (target != null && target.getID().equals(node.getID())) {
                    System.out.println("  [-] Succession consumed (fallback): " + nextSuccessions.get(i).getID());
                    nextSuccessions.remove(i);
                    break;
                }
            }
            return;
        }
        
        if (node.getIncomings().size() > 1 && !(node instanceof IControlNode)) {
            System.err.printf("  [WARNING] Implicit JoinNode detected in Action '%s'. " +
                    "(%d incoming edges) \n  The use of an explicit JoinNode is recommended.%n", 
                    node.getDeclaredName(), node.getIncomings().size());
        }
        
        for (ISuccession incoming : node.getIncomings()) {
            for (int i = 0; i < nextSuccessions.size(); i++) {
                if (nextSuccessions.get(i).getID().equals(incoming.getID())) {
                    System.out.println("  [-] Succession consumed: " + nextSuccessions.get(i).getID());
                    nextSuccessions.remove(i);
                    break; 
                }
            }
        }
    }

    protected void addOutgoings(INode node, List<ISuccession> nextSuccessions) {
        if (node.getOutgoings() == null) return;
        
        for (ISuccession outgoing : node.getOutgoings()) {
            boolean conditionMet = true;
            if (outgoing instanceof SuccessionAdapter sa) {
                conditionMet = sa.evaluateGuard();
            }
            if (conditionMet) {
                nextSuccessions.add(outgoing);
                System.out.println("  [+] Succession produced: " + outgoing.getID());
            }
        }
    }

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