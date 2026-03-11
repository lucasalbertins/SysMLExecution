package adapters.behavior.actions.nodes;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.nodes.INode;

public class MergeNodeCommand extends ActionNodeCommand {
    @Override
    public void execute(INode node, SysMLV2Configuration configuration) {
        // MergeNode: dispara com qualquer incoming ativo (diferente do Join)
        removeIncomings(node, configuration);
        addOutgoings(node, configuration);
    }
}
