package interfaces.behavior.actions.nodes;

import java.util.List;

import gamine.domain.SysMLV2Configuration;

public interface NodeCommand {
    // Retorna uma lista com os próximos estados possíveis a partir da execução deste nó
    List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration);
}