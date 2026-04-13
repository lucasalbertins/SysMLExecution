package gamine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import adapters.behavior.actions.ActionDefinitionAdapter;
import adapters.behavior.actions.ActionUsageAdapter;
import adapters.behavior.actions.SuccessionAdapter;
import adapters.behavior.actions.nodes.ControlNodeAdapter;
import adapters.behavior.actions.nodes.NodeCommandFactory;
import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.INamedElement;
import obp3.runtime.sli.SemanticRelation;

public class SysMLV2ActionSemantics implements SemanticRelation<INode, SysMLV2Configuration> {

    private ActionDefinitionAdapter actionDefinition;

    public SysMLV2ActionSemantics(ActionUsageAdapter usage) {
        this.actionDefinition = new ActionDefinitionAdapter(usage.getActionDefinition());
    }

    @Override
    public List<SysMLV2Configuration> initial() {
        System.out.println("\n[Initial] Buscando estado inicial");
        List<ISuccession> initialSuccessions = new ArrayList<>();
        List<IFlow> initialFlows = new ArrayList<>();
        
        for (INode node: actionDefinition.getNodes()) {
            if (node instanceof ControlNodeAdapter && ((ControlNodeAdapter)node).isInitialNode() ) {
                System.out.println("Succession inicial (source: start): " + node.getID());
                initialSuccessions.addAll(node.getOutgoings());
            }
        }
        return List.of(new SysMLV2Configuration(initialSuccessions, initialFlows));
    }

    @Override
    public List<INode> actions(SysMLV2Configuration configuration) {
        System.out.println("\n[Actions] Verificando actions disponíveis");
        Map<String, INode> enabledActions = new HashMap<>();

        // 1. Verifica alvos a partir das Successions (Fluxo de Controle)
        for (ISuccession succession : configuration.successions) {
            INode target = succession.getTarget();
            if (target == null) continue;
            
            if (!enabledActions.containsKey(target.getID()) && isNodeEnabled(target, configuration)) {
                System.out.println("  [!] Nó " + target.getDeclaredName() + " habilitado para execução!");
                enabledActions.put(target.getID(), target);
            }
        }
        // 2. Verifica alvos a partir dos Flows (Fluxo de Dados/Objeto)
        for (IFlow flow : configuration.flows) {
            IFlowEnd targetEnd = flow.getTarget();
            if (targetEnd == null || targetEnd.getReferencedFeature() == null) continue;

            // Busca o Nó correspondente usando a feature referenciada pelo FlowEnd
            INode targetNode = findNodeByFeature(targetEnd.getReferencedFeature());
            
            if (targetNode != null && !enabledActions.containsKey(targetNode.getID()) && isNodeEnabled(targetNode, configuration)) {
                //String payloadName = flow.getPayload() != null ? flow.getPayload().getDeclaredName() : "N/A";
                //System.out.println("  [!] Nó " + targetNode.getDeclaredName() + " habilitado por flow! Payload: " + payloadName);
                enabledActions.put(targetNode.getID(), targetNode);
            }
        }

        return new ArrayList<>(enabledActions.values());
    }

    // Utilitário para mapear uma Feature (vinda de um IFlowEnd) de volta para um INode
    private INode findNodeByFeature(INamedElement feature) {
        for (INode node : actionDefinition.getNodes()) {
            if (node.getID().equals(feature.getID())) {
                return node;
            }
        }
        return null;
    }

    // Tratamento de AND (Join/Action) e OR (Merge)
    private boolean isNodeEnabled(INode node, SysMLV2Configuration configuration) {
        if (node == null) return true;

        Set<String> activeSuccIds = configuration.successions.stream()
                .map(ISuccession::getID)
                .collect(Collectors.toSet());

        boolean isMergeNode = false;
        if (node instanceof ControlNodeAdapter) {
            isMergeNode = ((ControlNodeAdapter) node).isMergeNode();
        }

        // Verifica Lógica de Succession (Controle)
        boolean hasIncomingSuccessions = !node.getIncomings().isEmpty();
        boolean successionsEnabled = true;
        if (hasIncomingSuccessions) {
            if (isMergeNode) {
                // MERGE NODE: Basta UMA entrada estar com a succession
                successionsEnabled = node.getIncomings().stream()
                        .map(ISuccession::getID)
                        .anyMatch(activeSuccIds::contains);
            } else {
                // ACTION / JOIN NODE: TODAS as entradas precisam da succession
                successionsEnabled = node.getIncomings().stream()
                        .map(ISuccession::getID)
                        .allMatch(activeSuccIds::contains);
            }
        }
        // Lógica de Flow 
        boolean flowsEnabled = true;
        if (!node.getIncomingFlows().isEmpty()) {
             Set<String> activeFlowIds = configuration.flows.stream().map(IFlow::getID).collect(Collectors.toSet());
             flowsEnabled = node.getIncomingFlows().stream().map(IFlow::getID).allMatch(activeFlowIds::contains);
        }
        return successionsEnabled && flowsEnabled;
    }

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        if (node != null) {
            System.out.println("\n[Execute] Executando nó: " + node.getDeclaredName() + " via NodeCommandFactory");
            NodeCommandFactory.create(node).execute(node, configuration);
        }
        return List.of(calculateNextState(node, configuration));
    }

    private SysMLV2Configuration calculateNextState(INode node, SysMLV2Configuration current) {
        List<ISuccession> nextSuccessions = new ArrayList<>(current.successions);
        List<IFlow> nextFlows = new ArrayList<>(current.flows); // Propaga os flows
        
        // --- FASE DE CONSUMO ---
        // Consumo de Successions
        nextSuccessions.removeIf(token -> {
            INode target = token.getTarget();
            return target != null && target.getID().equals(node.getID());
        });

        // Consumo de Flows
        nextFlows.removeIf(flow -> {
            IFlowEnd targetEnd = flow.getTarget();
            if (targetEnd != null && targetEnd.getReferencedFeature() != null) {
                // Consome se a feature de destino deste flow é o Nó sendo executado
                return targetEnd.getReferencedFeature().getID().equals(node.getID());
            }
            return false;
        });

        // --- FASE DE PRODUÇÃO ---
        if (node != null) {
            // Produz Successions
            for (ISuccession out : node.getOutgoings()) {
                nextSuccessions.add((SuccessionAdapter) out);
            }
            // Produz Flows
            for (IFlow outFlow : node.getOutgoingFlows()) {
                nextFlows.add(outFlow);
            }
        }
        return new SysMLV2Configuration(nextSuccessions, nextFlows);
    }
}