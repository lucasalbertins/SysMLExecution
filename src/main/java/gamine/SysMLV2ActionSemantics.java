package gamine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.behavior.actions.nodes.NodeCommandFactory;
import adapters.utils.AdapterUtils;
import interfaces.behavior.actions.IActionDefinition;
import interfaces.behavior.actions.IActionUsage;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IControlNode;
import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.IFlowEnd;
import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.actions.nodes.INodeCommand;
import gamine.domain.SysMLV2Configuration;
import obp3.runtime.sli.SemanticRelation;

public class SysMLV2ActionSemantics implements SemanticRelation<INode, SysMLV2Configuration> {

	private IActionDefinition actionDefinition;
    private Map<String, INode> nodeRegistry;

    public SysMLV2ActionSemantics(IActionUsage usage) {
        this.actionDefinition = usage.getActionDefinition();
        this.nodeRegistry = new HashMap<>();
        
        if (actionDefinition != null && actionDefinition.getNodes() != null) {
            for (INode node : actionDefinition.getNodes()) {
                this.nodeRegistry.put(node.getID(), node);
            }
        }
        if (AdapterUtils.successions != null) {
            for (ISuccession succession : AdapterUtils.successions.values()) {
                    succession.setExecutionContext(usage); 
            }
        }
    }

    @Override
    public List<SysMLV2Configuration> initial() {
        System.out.println("\n[Initial] Searching initial state.");
        List<ISuccession> initialSuccessions = new ArrayList<>();
        List<IFlow> initialFlows = new ArrayList<>();
        
        if (actionDefinition != null && actionDefinition.getNodes() != null) {
        	for (INode node : actionDefinition.getNodes()) {
                if (node instanceof IControlNode && ((IControlNode)node).isStartNode()) {
                    System.out.println("  [!] StartNode reached: " + node.getID());
                    System.out.println("  [+] Succession produced: " + node.getOutgoings().getFirst().getID());
                    initialSuccessions.addAll(node.getOutgoings());
                }
            }
        }
        return List.of(new SysMLV2Configuration(initialSuccessions, initialFlows));
    }

    @Override
    public List<INode> actions(SysMLV2Configuration configuration) {
    	if (configuration.successions.isEmpty() && configuration.flows.isEmpty()) {
            return new ArrayList<>(); 
        }
        System.out.println("\n[Actions] Checking available actions.");
        Map<String, INode> enabledActions = new HashMap<>();

        // 1. Checks targets based on successions.
        for (ISuccession succession : configuration.successions) {
            INode dumbTarget = succession.getTarget();
            if (dumbTarget == null) continue;
            
            // getRealNode
            INode target = nodeRegistry.getOrDefault(dumbTarget.getID(), dumbTarget);
            
            // Checks if the node is enabled.
            if (!enabledActions.containsKey(target.getID()) && 
                NodeCommandFactory.create(target).isEnabled(target, configuration)) {
                System.out.println("  [!] Node '" + target.getDeclaredName() + "' enabled for execution!");
                enabledActions.put(target.getID(), target);
            }
        }
        
        // 2. Checks targets based on flows.
        for (IFlow flow : configuration.flows) {
            IFlowEnd targetEnd = flow.getTarget();
            if (targetEnd == null || targetEnd.getReferencedFeature() == null) continue;

            // findNodeByFeature
            INode targetNode = nodeRegistry.get(targetEnd.getReferencedFeature().getID());
            
            // Checks if the node is enabled.
            if (targetNode != null && !enabledActions.containsKey(targetNode.getID()) && 
                NodeCommandFactory.create(targetNode).isEnabled(targetNode, configuration)) {
                enabledActions.put(targetNode.getID(), targetNode);
            }
        }
        
        if (enabledActions.isEmpty()) {
            if (!configuration.successions.isEmpty()) {
                System.err.println("[SIMULATION ERROR] Deadlock/Starvation detected!");
                System.err.println("  [x] Stuck nodes:");
                
                for (ISuccession stuckToken : configuration.successions) {
                    String targetName = stuckToken.getTarget() != null ? stuckToken.getTarget().getDeclaredName() : "<unknown>";
                    System.err.println("      - " + targetName);
                }
                throw new IllegalStateException("Deadlock detected in SysML topology. Check for Join nodes or orphan paths.");
            }
        }
        return new ArrayList<>(enabledActions.values());
    }

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
    	if (node != null) {
            INodeCommand command = NodeCommandFactory.create(node);
            System.out.println("\n[Execute] Executing " + 
            		command.getClass().getSimpleName().replace("Command", "") 
            		+ ": " + node.getDeclaredName());
            return command.execute(node, configuration);
        }
        return List.of(configuration);
    }
}
