package adapters.behavior.actions.nodes;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.nodes.INode;

public class ForkNodeCommand extends NodeCommand {

    @Override
    public boolean isEnabled(INode node, SysMLV2Configuration configuration) {
        // ForkNode cannot have more than 1 entry.
        if (node != null && node.getIncomings() != null && node.getIncomings().size() > 1) {
            System.err.printf("  [ERROR] ForkNode '%s' has %d entries. " +
                    "SysML specification requires that a ForkNode have exactly 1 entry.%n", 
                    node.getDeclaredName(), node.getIncomings().size());
            return false;
        }
        return super.isEnabled(node, configuration);
    }
}
