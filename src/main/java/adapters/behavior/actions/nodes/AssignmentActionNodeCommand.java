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
        return super.execute(node, configuration);
    }
}
