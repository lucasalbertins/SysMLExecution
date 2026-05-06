package interfaces.behavior.actions.nodes;

import java.util.List;
import gamine.domain.SysMLV2Configuration;

public interface INodeCommand {
    
    // Checks if the topology allows this node to run.
    boolean isEnabled(INode node, SysMLV2Configuration configuration);
    
    // Consumes tokens, produces tokens, and returns the new state of the simulation.
    List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration);
}