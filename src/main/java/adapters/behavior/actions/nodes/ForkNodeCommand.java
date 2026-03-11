package adapters.behavior.actions.nodes;

import adapters.behavior.actions.SuccessionAdapter;
import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class ForkNodeCommand extends ActionNodeCommand {

    @Override
    public void execute(INode node, SysMLV2Configuration configuration) {
        removeIncomings(node, configuration);
        for (ISuccession outgoing : node.getOutgoings()) {
            configuration.successions.add((SuccessionAdapter) outgoing);
        }
    }
}
