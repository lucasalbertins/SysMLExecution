package adapters.behavior.actions.nodes;

import java.util.List;

import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.actions.nodes.INodeCommand;
import gamine.domain.SysMLV2Configuration;

public class TerminateActionNodeCommand implements INodeCommand {

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        System.err.println(" [!] TERMINATE ACTION REACHED: " + node.getDeclaredName());
        System.err.println("     Succession tokens destroyed: " + configuration.successions.size());
        System.err.println("     Flow tokens destroyed: " + configuration.flows.size());
        return List.of(configuration); 
    }
}