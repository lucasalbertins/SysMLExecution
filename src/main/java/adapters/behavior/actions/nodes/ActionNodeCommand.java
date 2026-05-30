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
        return super.execute(node, configuration);
    }

    protected void executeNestedAssignments(INode node) {
        if (node instanceof IActionUsage actionNode) {
            List<IAssignmentActionUsage> assignments = actionNode.getNestedAssignments();
            
            if (assignments != null) {
                for (IAssignmentActionUsage assignment : assignments) {
                    assignment.applyAssignment(); // Delegate to adapter
                }
            }
        }
    }
}
