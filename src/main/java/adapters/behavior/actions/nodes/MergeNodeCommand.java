package adapters.behavior.actions.nodes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class MergeNodeCommand extends ActionNodeCommand {
    
    @Override
    public boolean isEnabled(INode node, SysMLV2Configuration configuration) {
        if (node == null || node.getIncomings() == null || node.getIncomings().isEmpty()) return true;

        Set<String> activeSuccIds = configuration.successions.stream()
                .map(ISuccession::getID)
                .collect(Collectors.toSet());

        // (OR) logic: Only ONE token is needed to activate.
        return node.getIncomings().stream()
                .map(ISuccession::getID)
                .anyMatch(activeSuccIds::contains);
    }

    @Override
    protected void removeIncomings(INode node, List<ISuccession> nextSuccessions) {
        // MergeNode consumes ONLY 1 succession.
        for (ISuccession incoming : node.getIncomings()) {
            for (int i = 0; i < nextSuccessions.size(); i++) {
                if (nextSuccessions.get(i).getID().equals(incoming.getID())) {
                    System.out.println("  [-] Succession consumed (Merge): " + nextSuccessions.get(i).getID());
                    nextSuccessions.remove(i);
                    return; // Returns immediately after consuming 1 token.
                }
            }
        }
    }
}