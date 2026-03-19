package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import adapters.behavior.actions.SuccessionAdapter;
import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.actions.nodes.NodeCommand;

public class ActionNodeCommand implements NodeCommand {
    
    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        // 1. Cria uma cópia alterável das succcessions atuais
        List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
        
        // 2. Manipula a cópia
        removeIncomings(node, nextSuccessions);
        addOutgoings(node, nextSuccessions);
        
        // 3. Retorna o novo estado (que será congelado no construtor de SysMLV2Configuration)
        return List.of(new SysMLV2Configuration(nextSuccessions));
    }

    protected void removeIncomings(INode node, List<ISuccession> nextSuccessions) {
        for (ISuccession incoming : node.getIncomings()) {
            // Remove EXATAMENTE 1 succession por aresta de entrada
            for (int i = 0; i < nextSuccessions.size(); i++) {
                if (nextSuccessions.get(i).getID().equals(incoming.getID())) {
                    System.out.println("Consumido: " + nextSuccessions.get(i).getID());
                    nextSuccessions.remove(i);
                    break; // Interrompe para não consumir successions duplicados na mesma aresta
                }
            }
        }
    }

    protected void addOutgoings(INode node, List<ISuccession> nextSuccessions) {
        for (ISuccession outgoing : node.getOutgoings()) {
            nextSuccessions.add((SuccessionAdapter) outgoing);
            System.out.println("Produzido: " + outgoing.getID());
        }
    }
}