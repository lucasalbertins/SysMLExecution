package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;

import adapters.behavior.actions.SuccessionAdapter;
import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.actions.nodes.NodeCommand;

public class ActionNodeCommand implements NodeCommand {
    
    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        // 1. Cria cópias alteráveis das successions e flows atuais
        List<ISuccession> nextSuccessions = new ArrayList<>(configuration.successions);
        List<IFlow> nextFlows = new ArrayList<>(configuration.flows);
        
        // 2. Manipula a cópia de Successions (Fluxo de Controle)
        removeIncomings(node, nextSuccessions);
        addOutgoings(node, nextSuccessions);
        
        // 3. Manipula a cópia de Flows (Fluxo de Objetos/Dados)
        removeIncomingFlows(node, nextFlows);
        addOutgoingFlows(node, nextFlows);
        
        // 4. Retorna o novo estado usando o construtor completo
        return List.of(new SysMLV2Configuration(nextSuccessions, nextFlows));
    }

    protected void removeIncomings(INode node, List<ISuccession> nextSuccessions) {
        if (node.getIncomings() == null) return;
        
        for (ISuccession incoming : node.getIncomings()) {
            // Remove EXATAMENTE 1 succession por aresta de entrada
            for (int i = 0; i < nextSuccessions.size(); i++) {
                if (nextSuccessions.get(i).getID().equals(incoming.getID())) {
                    System.out.println("  [-] Consumido Succession: " + nextSuccessions.get(i).getID());
                    nextSuccessions.remove(i);
                    break; // Interrompe para não consumir successions duplicados na mesma aresta
                }
            }
        }
    }

    protected void addOutgoings(INode node, List<ISuccession> nextSuccessions) {
        if (node.getOutgoings() == null) return;
        
        for (ISuccession outgoing : node.getOutgoings()) {
            nextSuccessions.add((SuccessionAdapter) outgoing);
            System.out.println("  [+] Produzido Succession: " + outgoing.getID());
        }
    }

    // --- MÉTODOS DE FLOWS ---

    protected void removeIncomingFlows(INode node, List<IFlow> nextFlows) {
        if (node.getIncomingFlows() == null) return;
        
        for (IFlow incomingFlow : node.getIncomingFlows()) {
            for (int i = 0; i < nextFlows.size(); i++) {
                if (nextFlows.get(i).getID().equals(incomingFlow.getID())) {
                    String payloadName = incomingFlow.getPayload() != null ? incomingFlow.getPayload().getDeclaredName() : "<no-payload>";
                    System.out.printf("  [-] Consumido Flow %s: %s [Payload: %s]%n", 
                    		nextFlows.get(i).getDeclaredName(), 
                    		nextFlows.get(i).getID(), 
                            payloadName);
                    nextFlows.remove(i);
                    break;
                }
            }
        }
    }

    protected void addOutgoingFlows(INode node, List<IFlow> nextFlows) {
        if (node.getOutgoingFlows() == null) return;
        
        for (IFlow outgoingFlow : node.getOutgoingFlows()) {
            nextFlows.add(outgoingFlow);
            String payloadName = outgoingFlow.getPayload() != null ? outgoingFlow.getPayload().getDeclaredName() : "<no-payload>";
            System.out.printf("  [+] Produzido Flow %s: %s [Payload: %s]%n", 
            		outgoingFlow.getDeclaredName(), 
            		outgoingFlow.getID(), 
                    payloadName);
        }
    }
}