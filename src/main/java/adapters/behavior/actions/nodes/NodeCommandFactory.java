package adapters.behavior.actions.nodes;

import interfaces.behavior.actions.nodes.IControlNode;
import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.actions.nodes.NodeCommand;

public class NodeCommandFactory {

    public static NodeCommand create(INode node) {
        if (node instanceof IControlNode controlNode) {
            if (controlNode.isJoinNode())
                return new JoinNodeCommand();
            if (controlNode.isForkNode())
                return new ForkNodeCommand();
            if (controlNode.isMergeNode())
                return new MergeNodeCommand();
            if (controlNode.isDecisionNode())
                return new DecisionNodeCommand();
            if (controlNode.isFinalNode()) {
            	return new FinalNodeCommand();
            }
        }
        return new ActionNodeCommand(); 
    }
}