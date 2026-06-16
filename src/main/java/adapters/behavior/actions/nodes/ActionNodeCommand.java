package adapters.behavior.actions.nodes;

import java.util.List;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.IActionUsage;
import interfaces.behavior.actions.IAssignmentActionUsage;
import interfaces.behavior.actions.nodes.INode;

public class ActionNodeCommand extends NodeCommand {

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        executeNestedAssignments(node);
        List<SysMLV2Configuration> nextConfigs = super.execute(node, configuration);
        
        // For each generated configuration, we will ask the node to update the memory with the new values ​​that have just been calculated.
        for (SysMLV2Configuration nextConfig : nextConfigs) {
            updateMemorySnapshot(node, nextConfig);
        }
        
        return nextConfigs;
    }

    protected void executeNestedAssignments(INode node) {
        if (node instanceof IActionUsage actionNode) {
            List<IAssignmentActionUsage> assignments = actionNode.getNestedAssignments();
            if (assignments != null) {
                for (IAssignmentActionUsage assignment : assignments) {
                    assignment.applyAssignment();
                }
            }
        }
    }

    // We read the new values ​​and write them to the map of the current configuration.
    protected void updateMemorySnapshot(INode node, SysMLV2Configuration config) {
        if (node instanceof IActionUsage actionNode) {
            List<IAssignmentActionUsage> assignments = actionNode.getNestedAssignments();
            if (assignments != null) {
                for (IAssignmentActionUsage assignment : assignments) {
                    String varName = assignment.getTargetName(); 
                    Object varValue = assignment.getCurrentValue();
                    
                    if (varName != null && varValue != null) {
                        config.memory.put(varName, varValue);
                    }
                }
            }
        }
    }
}