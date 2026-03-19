package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import adapters.behavior.actions.SuccessionAdapter;
import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class DecisionNodeCommand extends ActionNodeCommand {

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        List<SysMLV2Configuration> possibleNextStates = new ArrayList<>();

        // Para cada saída possível, gera um estado novo e independente.
        for (ISuccession outgoing : node.getOutgoings()) {
            // 1. Cria cópia do estado atual
            List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
            
            // 2. Consome os tokens de entrada
            removeIncomings(node, nextSuccessions);
            
            // 3. Produz o token APENAS neste caminho específico
            nextSuccessions.add(outgoing);
            System.out.println("Decision produziu token no caminho: " + outgoing.getID());
            
            // 4. Adiciona como um possível futuro
            possibleNextStates.add(new SysMLV2Configuration(nextSuccessions));
        }

        return possibleNextStates; // Retorna estados
    }
}