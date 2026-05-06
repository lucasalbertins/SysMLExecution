package gamine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        
        for (INode node : actionDefinition.getNodes()) {
            if (node instanceof ControlNodeAdapter && ((ControlNodeAdapter)node).isStartNode() ) {
                System.out.println("Initial node (start): " + node.getID());
                initialSuccessions.addAll(node.getOutgoings());
            }
        }
        return List.of(new SysMLV2Configuration(initialSuccessions, initialFlows));
    }
    
    // Retrieves the node (with the correct interfaces) using the generic arrow node.
    private INode getRealNode(INode dumbNode) {
        if (dumbNode == null) return null;
        for (INode realNode : actionDefinition.getNodes()) {
            if (realNode.getID().equals(dumbNode.getID())) {
                return realNode; // Returns the ActionUsageAdapter or ControlNodeAdapter.
            }
        }
        return dumbNode; // Fallback
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

    @Override
    public List<INode> actions(SysMLV2Configuration configuration) {
        System.out.println("\n[Actions] Checking available actions");
        Map<String, INode> enabledActions = new HashMap<>();

        // 1. Checks targets based on successions.
        for (ISuccession succession : configuration.successions) {
            INode dumbTarget = succession.getTarget();
            if (dumbTarget == null) continue;
            
            INode target = getRealNode(dumbTarget);
            
            // Checks if the node is enabled.
            if (!enabledActions.containsKey(target.getID()) && 
                NodeCommandFactory.create(target).isEnabled(target, configuration)) {
                System.out.println("  [!] Node " + target.getDeclaredName() + " enabled for execution!");
                enabledActions.put(target.getID(), target);
            }
        }
        
        // 2. Checks targets based on flows.
        for (IFlow flow : configuration.flows) {
            IFlowEnd targetEnd = flow.getTarget();
            if (targetEnd == null || targetEnd.getReferencedFeature() == null) continue;

            INode targetNode = findNodeByFeature(targetEnd.getReferencedFeature());
            
            // Checks if the node is enabled.
            if (targetNode != null && !enabledActions.containsKey(targetNode.getID()) && 
                NodeCommandFactory.create(targetNode).isEnabled(targetNode, configuration)) {
                enabledActions.put(targetNode.getID(), targetNode);
            }
        }
        
        if (enabledActions.isEmpty()) {
        	// If there are no enabled actions, but there are remaining succession tokens
            if (!configuration.successions.isEmpty()) {
                System.err.println("[ERROR] Deadlock/Starvation detected!");
                System.err.println("The simulation is unable to proceed, the following tokens are stuck:");
                
                for (ISuccession stuckToken : configuration.successions) {
                    String targetName = stuckToken.getTarget() != null ? stuckToken.getTarget().getDeclaredName() : "<unknown>";
                    System.err.println(" ----> " + targetName);
                }
                // Throws the exception to immediately interrupt
                throw new IllegalStateException("Deadlock detected in SysML topology. Check for Join nodes or orphan paths.");
            }
        }
        return new ArrayList<>(enabledActions.values());
    }

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        if (node != null) {
            System.out.println("\n[Execute] Executing node: " 
                    + node.getDeclaredName() 
                    + " via NodeCommandFactory");
            
            // The command processes and returns the configured lists.
            return NodeCommandFactory.create(node).execute(node, configuration);
        }
        return List.of(configuration);
    }
}