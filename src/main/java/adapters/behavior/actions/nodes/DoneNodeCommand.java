package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class DoneNodeCommand extends ActionNodeCommand {

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
        List<IFlow> nextFlows = new ArrayList<>(configuration.flows);
        
        removeIncomings(node, nextSuccessions);
        removeIncomingFlows(node, nextFlows);
        
        String nodeName = node.getDeclaredName() != null ? node.getDeclaredName() : node.getID();
        System.out.printf("  [Final] Node '%s' reached. Journey completed successfully!%n", nodeName);
        
        return List.of(new SysMLV2Configuration(nextSuccessions, nextFlows));
    }
}