package gamine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omg.sysml.lang.sysml.ActionDefinition;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;

import adapters.behavior.actions.ActionDefinitionAdapter;
import adapters.behavior.actions.ActionUsageAdapter;
import adapters.behavior.actions.SuccessionAdapter;
import adapters.behavior.actions.nodes.NodeCommandFactory;
import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;
import obp3.runtime.sli.SemanticRelation;

public class SysMLV2ActionSemantics implements SemanticRelation<Element, SysMLV2Configuration> {

    private final ActionDefinition actDef;
    private final ActionDefinitionAdapter actionDefinition;

    public SysMLV2ActionSemantics(ActionUsageAdapter usage) {
        this.actDef = usage.getActionDefinition();
        this.actionDefinition = new ActionDefinitionAdapter(actDef);
    }

    @Override
    public List<SysMLV2Configuration> initial() {
        System.out.println("\n[Initial] Buscando estado inicial");
        List<SuccessionAdapter> initialSuccessions = new ArrayList<>();

        for (Element elem : actDef.getOwnedElement()) {
            if (elem instanceof SuccessionAsUsage s) {
                for (Element node : s.getSource()) {
                    if (node.getDeclaredName().equals("start")) {
                        System.out.println("Produzindo token na succession inicial (source: start): " + s.getElementId());
                        initialSuccessions.add(new SuccessionAdapter(s));
                    }
                }
            }
        }
        return List.of(new SysMLV2Configuration(initialSuccessions));
    }

    @Override
    public List<Element> actions(SysMLV2Configuration configuration) {
        System.out.println("\n[Actions] Verificando actions disponíveis");
        Map<String, Element> enabledActions = new HashMap<>();

        for (SuccessionAdapter succession : configuration.successions) {
            INode target = succession.getTarget();
            if (target == null || target.getElement() == null) continue;
            if (!enabledActions.containsKey(target.getElement().getElementId()) && allIncomingsActive(target, target.getElement(), configuration)) {
                System.out.println("[!] Nó " + target.getDeclaredName() + " habilitado para execução!");
                enabledActions.put(target.getElement().getElementId(), target.getElement());
            }
        }
        return new ArrayList<>(enabledActions.values());
    }

    private boolean allIncomingsActive(INode node, Element nodeElement, SysMLV2Configuration configuration) {
        Set<String> requiredIncomingIds = new HashSet<>();
        
        if (node != null) {
            for (ISuccession inc : node.getIncomings()) requiredIncomingIds.add(inc.getID());
        }
        // Retorna true se TODAS as entradas obrigatórias estiverem ativas na configuration atual
        return requiredIncomingIds.stream().allMatch(id -> configuration.successions.stream().anyMatch(s -> s.getID().equals(id)));
    }

    @Override
    public List<SysMLV2Configuration> execute(Element element, SysMLV2Configuration configuration) {
        System.out.println("\n[Execute] Executando nó: " + element.getDeclaredName());
        
        INode node = findNode(element);
        if (node != null) {
            System.out.println("[Execute] Acionando execute via NodeCommandFactory");
            NodeCommandFactory.create(node).execute(node, configuration);
        }
        return List.of(calculateNextState(node, element, configuration));
    }

    private SysMLV2Configuration calculateNextState(INode node, Element nodeElement, SysMLV2Configuration current) {
        System.out.println("[State] Calculando próximo estado");
        List<SuccessionAdapter> nextSuccessions = new ArrayList<>(current.successions);
        
        // FASE DE CONSUMO
        nextSuccessions.removeIf(token -> {
            INode target = token.getTarget();
            if (target == null || target.getElement() == null) return false;

            // Tenta consumir batendo o ID exato
            if (target.getElement().getElementId().equals(nodeElement.getElementId())) {
                System.out.println("Consumido (por ID): " + token.getID());
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
            System.out.println("Produzido: " + outgoing.getID());
        }

        return new SysMLV2Configuration(nextSuccessions);
    }

    private INode findNode(Element element) {
        for (INode node : actionDefinition.getNodes()) {
            if (node.getID().equals(element.getElementId())) {
                return node;
            }
        }
        return null;
    }
}