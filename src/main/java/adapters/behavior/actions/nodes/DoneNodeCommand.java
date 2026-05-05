package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

import gamine.domain.SysMLV2Configuration;

public class DoneNodeCommand extends ActionNodeCommand {

	@Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        // 1. Creates copies of the current state lists.
        List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
        List<IFlow> nextFlows = new ArrayList<>(configuration.flows);
        
        // 2. Consumes the input tokens (Control and Data/Objects).
        removeIncomings(node, nextSuccessions);
        removeIncomingFlows(node, nextFlows);
        
        // 3. Customized log to correctly report that the path died.
        String nodeName = node.getDeclaredName() != null ? node.getDeclaredName() : node.getID();
        System.out.printf("  [Final] Node '%s' reached. Journey completed successfully!%n", nodeName);
        // Avoid the usage of addOutgoings() or addOutgoingFlows().
        
        // 4. Returns the state without the tokens that have just been consumed.
        return List.of(new SysMLV2Configuration(nextSuccessions, nextFlows));
    }
}
