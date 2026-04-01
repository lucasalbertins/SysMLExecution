package adapters.behavior.actions.nodes;

import java.util.List;

import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class MergeNodeCommand extends ActionNodeCommand {
	@Override
    protected void removeIncomings(INode node, List<ISuccession> nextSuccessions) {
        // MergeNode consome APENAS 1 token disponível
        for (ISuccession incoming : node.getIncomings()) {
            for (int i = 0; i < nextSuccessions.size(); i++) {
                if (nextSuccessions.get(i).getID().equals(incoming.getID())) {
                    System.out.println("Consumido: " + nextSuccessions.get(i).getID());
                    nextSuccessions.remove(i);
                    // Se achar o token e consumir, retorna imediato.
                    // Isso garante que ele só consome 1 entrada e para por aí.
                    return; 
                }
            }
        }
    }
}