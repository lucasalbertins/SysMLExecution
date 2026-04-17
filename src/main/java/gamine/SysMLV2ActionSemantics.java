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
import adapters.utils.AdapterUtils;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.INamedElement;

import gamine.domain.SysMLV2Configuration;
import obp3.runtime.sli.SemanticRelation;

public class SysMLV2ActionSemantics implements SemanticRelation<INode, SysMLV2Configuration> {

    private ActionDefinitionAdapter actionDefinition;

    public SysMLV2ActionSemantics(ActionUsageAdapter usage) {
        this.actionDefinition = new ActionDefinitionAdapter(usage.getActionDefinition());
        if (AdapterUtils.successions != null) {
            for (ISuccession succession : AdapterUtils.successions.values()) {
                if (succession instanceof SuccessionAdapter sa) {
                    sa.setExecutionContext(usage); 
                }
            }
        }
    }

    @Override
    public List<SysMLV2Configuration> initial() {
        System.out.println("\n[Initial] Searching initial state");
        List<ISuccession> initialSuccessions = new ArrayList<>();
        List<IFlow> initialFlows = new ArrayList<>();
        
        for (INode node: actionDefinition.getNodes()) {
            if (node instanceof ControlNodeAdapter && ((ControlNodeAdapter)node).isInitialNode() ) {
                System.out.println("Initial succession (source: start): " + node.getID());
                initialSuccessions.addAll(node.getOutgoings());
            }
        }
        return List.of(new SysMLV2Configuration(initialSuccessions, initialFlows));
    }

    @Override
    public List<INode> actions(SysMLV2Configuration configuration) {
        System.out.println("\n[Actions] Checking available actions");
        Map<String, INode> enabledActions = new HashMap<>();

        // 1. Checks targets based on successions.
        for (ISuccession succession : configuration.successions) {
            INode target = succession.getTarget();
            if (target == null) continue;
            
            if (!enabledActions.containsKey(target.getID()) && isNodeEnabled(target, configuration)) {
                System.out.println("  [!] Node " + target.getDeclaredName() + " enabled for execution!");
                enabledActions.put(target.getID(), target);
            }
        }
        // 2. Checks targets based on flows.
        for (IFlow flow : configuration.flows) {
            IFlowEnd targetEnd = flow.getTarget();
            if (targetEnd == null || targetEnd.getReferencedFeature() == null) continue;

            // Finds the corresponding node using the feature referenced by FlowEnd.
            INode targetNode = findNodeByFeature(targetEnd.getReferencedFeature());
            
            if (targetNode != null && !enabledActions.containsKey(targetNode.getID()) && isNodeEnabled(targetNode, configuration)) {
                enabledActions.put(targetNode.getID(), targetNode);
            }
        }
        return new ArrayList<>(enabledActions.values());
    }

    // Utility to map a Feature (coming from an IFlowEnd) back to an INode.
    private INode findNodeByFeature(INamedElement feature) {
        for (INode node : actionDefinition.getNodes()) {
            if (node.getID().equals(feature.getID())) {
                return node;
            }
        }
        return null;
    }

    // Treatment of AND (Action/Join) and OR (Merge)
    private boolean isNodeEnabled(INode node, SysMLV2Configuration configuration) {
        if (node == null) return true;

        Set<String> activeSuccIds = configuration.successions.stream()
                .map(ISuccession::getID)
                .collect(Collectors.toSet());

        boolean isMergeNode = false;
        if (node instanceof ControlNodeAdapter) {
            isMergeNode = ((ControlNodeAdapter) node).isMergeNode();
        }

        // Verifies succession logic.
        boolean hasIncomingSuccessions = !node.getIncomings().isEmpty();
        boolean successionsEnabled = true;
        if (hasIncomingSuccessions) {
            if (isMergeNode) {
            	// MergeNode: Only ONE entry needs to have the succession.
                successionsEnabled = node.getIncomings().stream()
                        .map(ISuccession::getID)
                        .anyMatch(activeSuccIds::contains);
            } else {
            	// ActionNode/JoinNode: ALL entries need the succession.
                successionsEnabled = node.getIncomings().stream()
                        .map(ISuccession::getID)
                        .allMatch(activeSuccIds::contains);
            }
        }
        // Verifies flow logic. 
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
            System.out.println("\n[Execute] Executing node: " + node.getDeclaredName() + " via NodeCommandFactory");
            NodeCommandFactory.create(node).execute(node, configuration);
        }
        // Returns the structure itself using internal calculation.
        return List.of(calculateNextState(node, configuration));
    }

    private SysMLV2Configuration calculateNextState(INode node, SysMLV2Configuration current) {
        List<ISuccession> nextSuccessions = new ArrayList<>(current.successions);
        List<IFlow> nextFlows = new ArrayList<>(current.flows); 
        
        // --- CONSUMPTION PHASE ---
        // Consumption of successions.
        nextSuccessions.removeIf(token -> {
            INode target = token.getTarget();
            return target != null && target.getID().equals(node.getID());
        });
        // Consumption of flows.
        nextFlows.removeIf(flow -> {
            IFlowEnd targetEnd = flow.getTarget();
            if (targetEnd != null && targetEnd.getReferencedFeature() != null) {
            	// Consumes if the target feature of this flow is the node being executed.
                return targetEnd.getReferencedFeature().getID().equals(node.getID());
            }
            return false;
        });

        // --- PRODUCTION PHASE ---
        if (node != null) {
        	// Produces successions.
            for (ISuccession out : node.getOutgoings()) {
                // Checks the guard here to respect the main semantic block.
                boolean conditionMet = true;
                if (out instanceof SuccessionAdapter sa) {
                    conditionMet = sa.evaluateGuard();
                }
                
                if (conditionMet) {
                    nextSuccessions.add(out);
                }
            }
            // Produces flows.
            for (IFlow outFlow : node.getOutgoingFlows()) {
                nextFlows.add(outFlow);
            }
        }
        return new SysMLV2Configuration(nextSuccessions, nextFlows);
    }
}
