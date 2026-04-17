package adapters.behavior.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.impl.ActionUsageImpl;

public class ActionUsageAdapterRegistry {

	// TODO: Avoid the import of ActionUsageImpl (?)
    private final Map<String, ActionUsageAdapter> map = new HashMap<>();

    public ActionUsageAdapterRegistry(Namespace root) {
        collect(root);
    }

    // Returns based on the elementId extracted from an adapter via getElementId().
    public ActionUsageAdapter getById(String id) {
        return map.get(id);
    }

    // Returns a list of all adapters with the same declared name.
    public List<ActionUsageAdapter> getByDeclaredName(String name) {
        return map.values().stream()
                .filter(a -> name.equals(a.getDeclaredName()))
                .toList();
    }

    // Returns all adapters.
    public Collection<ActionUsageAdapter> getAll() {
        return map.values();
    }
    
    // Returns based on the position assumed on the map. 
    public ActionUsageAdapter getByPosition(int index) {
        return map.values()
                  .stream()
                  .toList()
                  .get(index);
    }

    // Collects the ActionUsage's present in the SysML model.
    private void collect(Element element) {
        if (element instanceof ActionUsageImpl ad) {
        	map.put(
                ad.getElementId(),
                new ActionUsageAdapter(ad)
            );
        }
        if (element instanceof Namespace ns) {
            for (Element member : ns.getOwnedMember()) {
                collect(member);
            }
        }
    }
}
