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
import interfaces.behavior.actions.IActionUsage;
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
                return realNode; // Returns the ActionUsageAdapter or ControlNodeAdapter!
            }
        }
        return dumbNode; // Fallback
    }

    @Override
    public List<INode> actions(SysMLV2Configuration configuration) {
        System.out.println("\n[Actions] Checking available actions");
        Map<String, INode> enabledActions = new HashMap<>();

        // 1. Checks targets based on successions.
        for (ISuccession succession : configuration.successions) {
            INode dumbTarget = succession.getTarget();
            if (dumbTarget == null) continue;
            
            // We swap the generic for the high-end.
            INode target = getRealNode(dumbTarget);
            
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
        
        if (enabledActions.isEmpty()) {
            // If there are no enabled actions, but there are remaining succession tokens...
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
            System.out.println("\n[Execute] Executing node: " 
            		+ node.getDeclaredName() 
            		+ " via NodeCommandFactory");
            NodeCommandFactory.create(node).execute(node, configuration);
        }
        // Returns the structure itself using internal calculation.
        return List.of(calculateNextState(node, configuration));
    }

    private SysMLV2Configuration calculateNextState(INode node, SysMLV2Configuration current) {
        List<ISuccession> nextSuccessions = new ArrayList<>(current.successions);
        List<IFlow> nextFlows = new ArrayList<>(current.flows); 
        
        // --- CONSUMPTION PHASE ---
        if (node != null) {
            boolean isMerge = false;
            boolean isDone = false;
            boolean isTerminate = false;

            if (node instanceof ControlNodeAdapter) {
                isMerge = ((ControlNodeAdapter) node).isMergeNode();
                isDone = ((ControlNodeAdapter) node).isDoneNode();
            }
            
            if (node instanceof IActionUsage actionUsage) {
                isTerminate = actionUsage.isTerminateNode();
            }

            // 1. TERMINATE
            if (isTerminate) {
                nextSuccessions.clear();
                nextFlows.clear();
                return new SysMLV2Configuration(nextSuccessions, nextFlows);
            }
            
            // 2. MERGE/DONE
            else if (isMerge || isDone) {
                for (int i = 0; i < nextSuccessions.size(); i++) {
                    INode target = nextSuccessions.get(i).getTarget();
                    if (target != null && target.getID().equals(node.getID())) {
                        nextSuccessions.remove(i);
                        break; 
                    }
                }
            } 
            // 3. ACTIONS/JOINS
            else {
                if (node.getIncomings() != null && !node.getIncomings().isEmpty()) {
                    for (ISuccession incomingEdge : node.getIncomings()) {
                        for (int i = 0; i < nextSuccessions.size(); i++) {
                            if (nextSuccessions.get(i).getID().equals(incomingEdge.getID())) {
                                nextSuccessions.remove(i);
                                break; 
                            }
                        }
                    }
                } else {
                    // 4. Fallback
                    for (int i = 0; i < nextSuccessions.size(); i++) {
                        INode target = nextSuccessions.get(i).getTarget();
                        if (target != null && target.getID().equals(node.getID())) {
                            nextSuccessions.remove(i);
                            break;
                        }
                    }
                }
            }

            if (node.getIncomingFlows() != null) {
                for (IFlow incomingFlow : node.getIncomingFlows()) {
                    for (int i = 0; i < nextFlows.size(); i++) {
                        if (nextFlows.get(i).getID().equals(incomingFlow.getID())) {
                            nextFlows.remove(i);
                            break;
                        }
                    }
                }
            }
        }

        // --- PRODUCTION PHASE ---
        if (node != null) {
            if (node.getOutgoings() != null) {
                for (ISuccession out : node.getOutgoings()) {
                    boolean conditionMet = true;
                    if (out instanceof SuccessionAdapter sa) {
                        conditionMet = sa.evaluateGuard();
                    }
                    
                    if (conditionMet) {
                        nextSuccessions.add(out);
                    }
                }
            }
            
            if (node.getOutgoingFlows() != null) {
                for (IFlow outFlow : node.getOutgoingFlows()) {
                    nextFlows.add(outFlow);
                }
            }
        }
        
        return new SysMLV2Configuration(nextSuccessions, nextFlows);
    }
}
