package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import adapters.behavior.actions.SuccessionAdapter;
import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;

public class DecisionNodeCommand extends ActionNodeCommand {

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        List<SysMLV2Configuration> possibleNextStates = new ArrayList<>();

        for (ISuccession outgoing : node.getOutgoings()) {
            
        	// 1. Analisa a guarda antes de criar o caminho
            boolean isConditionMet = true;
            if (outgoing instanceof SuccessionAdapter adapter) {
                isConditionMet = adapter.evaluateGuard();
            } 

            if (isConditionMet) {
            	// Caminho aprovado pela guarda (ou não possui guarda)
                List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
                List<IFlow> nextFlows = new ArrayList<>(configuration.flows);
                
                removeIncomings(node, nextSuccessions);
                removeIncomingFlows(node, nextFlows);
                
                nextSuccessions.add(outgoing);
                addOutgoingFlows(node, nextFlows);
                
                System.out.printf("  [Decision] Caminho PERMITIDO. Produziu Succession: %s%n", outgoing.getID());
                
                possibleNextStates.add(new SysMLV2Configuration(nextSuccessions, nextFlows));
            } else {
            	// Caminho rejeitado
                System.out.printf("  [Decision] Caminho BLOQUEADO pela guarda: %s%n", outgoing.getID());
            }
        }
        
        return possibleNextStates; 
    }
}