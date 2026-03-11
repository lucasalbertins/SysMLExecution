package adapters.behavior.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.impl.ActionUsageImpl;

public class ActionUsageAdapterRegistry {

    private final Map<String, ActionUsageAdapter> map = new HashMap<>();

    public ActionUsageAdapterRegistry(Namespace root) {
        collect(root);
    }

    // Retorna de acordo com o elementId extraído de um adaptador via getElementId()
    public ActionUsageAdapter getById(String id) {
        return map.get(id);
    }

    // Retorna uma lista com todos os adaptadores com o mesmo nome declarado
    public List<ActionUsageAdapter> getByDeclaredName(String name) {
        return map.values().stream()
                .filter(a -> name.equals(a.getDeclaredName()))
                .toList();
    }

    // Retorna todas os adaptadores
    public Collection<ActionUsageAdapter> getAll() {
        return map.values();
    }
    
    // Talvez desnecessário
    public ActionUsageAdapter getByPosition(int index) {
        return map.values()
                  .stream()
                  .toList()
                  .get(index);
    }

    // Coleta as ActionDefinition presente no modelo sysml
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
