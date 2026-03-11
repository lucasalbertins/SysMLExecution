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

    // Retorna de acordo com o elementId extraído de um adaptador via getElementId()
    public ActionDefinitionAdapter getById(String id) {
        return map.get(id);
    }

    // Retorna uma lista com todos os adaptadores com o mesmo nome declarado
    public List<ActionDefinitionAdapter> getByDeclaredName(String name) {
        return map.values().stream()
                .filter(a -> name.equals(a.getDeclaredName()))
                .toList();
    }

    // Retorna todas os adaptadores
    public Collection<ActionDefinitionAdapter> getAll() {
        return map.values();
    }
    
    // Talvez desnecessário
    public ActionDefinitionAdapter getByPosition(int index) {
        return map.values()
                  .stream()
                  .toList()
                  .get(index);
    }

    // Coleta as ActionDefinition presente no modelo sysml
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
