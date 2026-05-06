package adapters.behavior.actions.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;
import interfaces.behavior.actions.nodes.INodeCommand;

public class TerminateNodeCommand implements INodeCommand {

    @Override
    public boolean isEnabled(INode node, SysMLV2Configuration configuration) {
        if (node == null) return true;

        Set<String> activeTargetIds = configuration.successions.stream()
                .map(s -> s.getTarget() != null ? s.getTarget().getID() : "")
                .collect(Collectors.toSet());

        // (OR): If any succession points to him, he activates it.
        return activeTargetIds.contains(node.getID());
    }

    @Override
    public List<SysMLV2Configuration> execute(INode node, SysMLV2Configuration configuration) {
        System.err.println(" [!] TERMINATE ACTION REACHED: " + (node.getDeclaredName() != null ? node.getDeclaredName() : "<no-declared-name>"));
        System.err.println("     Succession tokens destroyed: " + configuration.successions.size());
        for (ISuccession succession : configuration.successions) {
            System.err.println("     " + succession.getID());
        }
        System.err.println("     Flow tokens destroyed: " + configuration.flows.size());
        
        // Returns completely cleared state lists.
        return List.of(new SysMLV2Configuration(new ArrayList<>(), new ArrayList<>())); 
    }
}