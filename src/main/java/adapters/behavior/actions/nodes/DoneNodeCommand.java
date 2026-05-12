package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class DoneNodeCommand extends NodeCommand {

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
        List<IFlow> nextFlows = new ArrayList<>(configuration.flows);
        
        removeIncomings(node, nextSuccessions);
        removeIncomingFlows(node, nextFlows);
        
        System.out.println("  [!] DoneNode reached. Journey completed successfully!");
        return List.of(new SysMLV2Configuration(nextSuccessions, nextFlows));
    }
}
