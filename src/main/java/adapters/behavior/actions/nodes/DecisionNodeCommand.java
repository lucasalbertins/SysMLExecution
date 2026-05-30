package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;

public class DecisionNodeCommand extends NodeCommand {

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        List<SysMLV2Configuration> possibleNextStates = new ArrayList<>();

        for (ISuccession outgoing : node.getOutgoings()) {
            boolean isConditionMet = true;
            if (outgoing instanceof ISuccession adapter) {
                isConditionMet = adapter.evaluateGuard();
            }
            
            if (isConditionMet) {
                List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
                List<IFlow> nextFlows = new ArrayList<>(configuration.flows);
                
                removeIncomings(node, nextSuccessions);
                removeIncomingFlows(node, nextFlows);
                
                nextSuccessions.add(outgoing);
                addOutgoingFlows(node, nextFlows);
                
                System.out.printf("  [o] Path allowed.\n  [+] Succession produced: %s%n", outgoing.getID());
                possibleNextStates.add(new SysMLV2Configuration(nextSuccessions, nextFlows));
            } else {
                System.out.printf("  [x] Path blocked by guard: %s%n", outgoing.getID());
            }
        }
        return possibleNextStates; 
    }
}
