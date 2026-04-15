package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

public class FinalNodeCommand extends ActionNodeCommand {

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        // 1. Cria cópias das listas de estado atuais
        List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
        List<IFlow> nextFlows = new ArrayList<>(configuration.flows);
        
        // 2. Consome os tokens de entrada (Controle e Dados/Objetos)
        removeIncomings(node, nextSuccessions);
        removeIncomingFlows(node, nextFlows);
        
        // 3. Log customizado para informar que o caminho morreu corretamente
        String nodeName = node.getDeclaredName() != null ? node.getDeclaredName() : node.getID();
        System.out.printf("  [Final] Nó '%s' alcançado. Caminho encerrado com sucesso!%n", nodeName);
        
        // Evitar addOutgoings() e addOutgoingFlows()
        
        // 4. Retorna o estado sem os tokens que acabaram de ser consumidos
        return List.of(new SysMLV2Configuration(nextSuccessions, nextFlows));
    }
}