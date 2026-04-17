package interfaces.behavior.actions.nodes;

import java.util.List;

import gamine.domain.SysMLV2Configuration;

public interface INodeCommand {
    // Returns a list of the next possible states after the execution of this node.
    List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration);
}
