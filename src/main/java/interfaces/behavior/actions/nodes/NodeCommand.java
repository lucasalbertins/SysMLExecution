package interfaces.behavior.actions.nodes;

import gamine.domain.SysMLV2Configuration;

public interface NodeCommand {
	void execute(INode node, SysMLV2Configuration configuration);
}