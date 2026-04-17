package adapters.behavior.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.sysml.lang.sysml.ActionDefinition;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;

public class ActionDefinitionAdapterRegistry {

    private Map<String, ActionDefinitionAdapter> map = new HashMap<>();

    public ActionDefinitionAdapterRegistry(Namespace root) {
        collect(root);
    }

    // Returns based on the elementId extracted from an adapter via getElementId().
    public ActionDefinitionAdapter getById(String id) {
        return map.get(id);
    }

    // Returns a list of all adapters with the same declared name.
    public List<ActionDefinitionAdapter> getByDeclaredName(String name) {
        return map.values().stream()
                .filter(a -> name.equals(a.getDeclaredName()))
                .toList();
    }

    // Returns all adapters.
    public Collection<ActionDefinitionAdapter> getAll() {
        return map.values();
    }
    
    // Returns based on the position assumed on the map.
    public ActionDefinitionAdapter getByPosition(int index) {
        return map.values()
                  .stream()
                  .toList()
                  .get(index);
    }

    // Collects the ActionDefinition's present in the SysML model.
    private void collect(Element element) {
        if (element instanceof ActionDefinition ad) {
        	map.put(
                ad.getElementId(),
                new ActionDefinitionAdapter(ad)
            );
        }
        if (element instanceof Namespace ns) {
            for (Element member : ns.getOwnedMember()) {
                collect(member);
            }
        }
    }
}
