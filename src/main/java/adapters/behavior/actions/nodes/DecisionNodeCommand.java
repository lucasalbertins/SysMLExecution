package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import adapters.behavior.actions.SuccessionAdapter;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;

import gamine.domain.SysMLV2Configuration;

public class DecisionNodeCommand extends ActionNodeCommand {

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        List<SysMLV2Configuration> possibleNextStates = new ArrayList<>();

        for (ISuccession outgoing : node.getOutgoings()) {
        	// 1. Analyzes the guard before creating the path.
            boolean isConditionMet = true;
            if (outgoing instanceof SuccessionAdapter adapter) {
                isConditionMet = adapter.evaluateGuard();
            }
            // Path approved by the guard (or not guarded).
            if (isConditionMet) {
                List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
                List<IFlow> nextFlows = new ArrayList<>(configuration.flows);
                
                removeIncomings(node, nextSuccessions);
                removeIncomingFlows(node, nextFlows);
                
                nextSuccessions.add(outgoing);
                addOutgoingFlows(node, nextFlows);
                
                System.out.printf("  [Decision] Path allowed. [+] Succession produced: %s%n", outgoing.getID());
                possibleNextStates.add(new SysMLV2Configuration(nextSuccessions, nextFlows));
            } else {
            	// Rejected path.
                System.out.printf("  [Decision] Path blocked by guard: %s%n", outgoing.getID());
            }
        }
        return possibleNextStates; 
    }
}
