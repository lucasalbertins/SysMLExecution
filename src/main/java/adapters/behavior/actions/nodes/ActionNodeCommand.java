package adapters.behavior.actions.nodes;

import adapters.behavior.actions.SuccessionAdapter;
import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.actions.nodes.NodeCommand;

public class ActionNodeCommand implements NodeCommand {
	public void execute(INode node, SysMLV2Configuration configuration) {
        removeIncomings(node, configuration);
        addOutgoings(node, configuration);
    }

    protected void removeIncomings(INode node, SysMLV2Configuration configuration) {
        for (ISuccession incoming : node.getIncomings()) {
            configuration.successions.removeIf(
                s -> s.getID().equals(incoming.getID())
            );
        }
    }

    protected void addOutgoings(INode node, SysMLV2Configuration configuration) {
        for (ISuccession outgoing : node.getOutgoings()) {
            configuration.successions.add((SuccessionAdapter) outgoing);
        }
    }
}
