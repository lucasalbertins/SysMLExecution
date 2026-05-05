package adapters.behavior.actions.nodes;

import interfaces.behavior.actions.nodes.IControlNode;
import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.actions.nodes.INodeCommand;

public class NodeCommandFactory {

    public static INodeCommand create(INode node) {
        if (node instanceof IControlNode controlNode) {
            if (controlNode.isJoinNode())
                return new JoinNodeCommand();
            if (controlNode.isForkNode())
                return new ForkNodeCommand();
            if (controlNode.isMergeNode())
                return new MergeNodeCommand();
            if (controlNode.isDecisionNode())
                return new DecisionNodeCommand();
            if (controlNode.isDoneNode())
            	return new DoneNodeCommand();
            if (controlNode.isTerminateNode()) // Maybe not the best way to organize.
            	return new TerminateActionNodeCommand();
        }
        return new ActionNodeCommand(); 
    }
}
