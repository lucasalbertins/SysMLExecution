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
import interfaces.behavior.actions.nodes.INode;
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
        
        for (INode node: actionDefinition.getNodes()) {
        	if (node instanceof ControlNodeAdapter && 
        			((ControlNodeAdapter)node).isInitialNode() ) {
        		System.out.println("Produzindo token na succession inicial (source: start): " + node.getID());
        		initialSuccessions.addAll(node.getOutgoings());
			}
        }
        return List.of(new SysMLV2Configuration(initialSuccessions));
    }

    @Override
    public List<INode> actions(SysMLV2Configuration configuration) {
        System.out.println("\n[Actions] Verificando actions disponíveis");
        Map<String, INode> enabledActions = new HashMap<>();

        for (ISuccession succession : configuration.successions) {
            INode target = succession.getTarget();
            if (target == null) continue;
            
            // Troquei allIncomingsActive por isNodeEnabled, que analisa MergeNode
            if (!enabledActions.containsKey(target.getID()) && isNodeEnabled(target, configuration)) {
                System.out.println("[!] Nó " + target.getDeclaredName() + " habilitado para execução!");
                enabledActions.put(target.getID(), target);
            }
        }
        return new ArrayList<>(enabledActions.values());
    }

    // NOVO MÉTODO: Trata AND e OR
    private boolean isNodeEnabled(INode node, SysMLV2Configuration configuration) {
        if (node == null || node.getIncomings().isEmpty()) return true;

        Set<String> activeIds = configuration.successions.stream()
                .map(ISuccession::getID)
                .collect(Collectors.toSet());

        // Verifica se é um MergeNode
        boolean isMergeNode = false;
        if (node instanceof ControlNodeAdapter) {
            isMergeNode = ((ControlNodeAdapter) node).isMergeNode();
        }

        if (isMergeNode) {
            // MERGE NODE: Basta UMA entrada estar com token
            return node.getIncomings().stream()
                    .map(ISuccession::getID)
                    .anyMatch(activeIds::contains);
        } else {
            // ACTION NODE / JOIN NODE: TODAS as entradas precisam de token
            return node.getIncomings().stream()
                    .map(ISuccession::getID)
                    .allMatch(activeIds::contains);
        }
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
        //System.out.println("[State] Calculando próximo estado");
        List<ISuccession> nextSuccessions = new ArrayList<>(current.successions);
        
        // FASE DE CONSUMO
        nextSuccessions.removeIf(token -> {
            INode target = token.getTarget();
            if (target == null) return false;

            // Tenta consumir batendo o ID exato
            if (target.getID().equals(node.getID())) {
                //System.out.println("Consumido (por ID): " + token.getID());
                return true;
            }
            return false;
        });
        // FASE DE PRODUÇÃO
        List<SuccessionAdapter> outgoings = new ArrayList<>();
        if (node != null) {
            for (ISuccession out : node.getOutgoings()) {
                outgoings.add((SuccessionAdapter) out);
            }
        }
        for (SuccessionAdapter outgoing : outgoings) {
            nextSuccessions.add(outgoing);
            //System.out.println("Produzido: " + outgoing.getID());
        }
        return new SysMLV2Configuration(nextSuccessions);
    }
}