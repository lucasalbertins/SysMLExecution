package adapters.behavior.actions.nodes;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.nodes.INode;

public class JoinNodeCommand extends ActionNodeCommand {

    @Override
    public void execute(INode node, SysMLV2Configuration configuration) {
        removeIncomings(node, configuration);
        addOutgoings(node, configuration);
    }
}
