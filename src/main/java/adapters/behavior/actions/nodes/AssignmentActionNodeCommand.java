package adapters.behavior.actions.nodes;

import java.util.List;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.IAssignmentActionUsage;
import interfaces.behavior.actions.nodes.INode;

public class AssignmentActionNodeCommand extends ActionNodeCommand {

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        if (node instanceof IAssignmentActionUsage assignmentNode) {
            assignmentNode.applyAssignment();
        }
        List<SysMLV2Configuration> nextConfigs = super.execute(node, configuration);
        
        if (node instanceof IAssignmentActionUsage assignmentNode) {
            for (SysMLV2Configuration nextConfig : nextConfigs) {
                String varName = assignmentNode.getTargetName();
                Object varValue = assignmentNode.getCurrentValue();
                
                if (varName != null && varValue != null) {
                    nextConfig.memory.put(varName, varValue);
                }
            }
        }
        
        return nextConfigs;
    }
}