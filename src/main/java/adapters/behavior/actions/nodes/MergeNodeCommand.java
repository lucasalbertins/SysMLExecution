package adapters.behavior.actions.nodes;

import java.util.List;

import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class MergeNodeCommand extends ActionNodeCommand {
	
	@Override
    protected void removeIncomings(INode node, List<ISuccession> nextSuccessions) {
        // MergeNode consumes ONLY 1 available succession
        for (ISuccession incoming : node.getIncomings()) {
            for (int i = 0; i < nextSuccessions.size(); i++) {
                if (nextSuccessions.get(i).getID().equals(incoming.getID())) {
                    System.out.println("  [-] Succession consumed: " + nextSuccessions.get(i).getID());
                    nextSuccessions.remove(i);
                    // If it finds the succession and consumes it, it returns immediately.
                    // This ensures that it only consumes 1 input and stops there.
                    return; 
                }
            }
        }
    }
}
