package gamine.domain;

import java.util.ArrayList;
import java.util.List;

import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IFlow;

public class SysMLV2Configuration {

    public List<ISuccession> successions;
    public List<IFlow> flows;

    public SysMLV2Configuration(List<ISuccession> succs, List<IFlow> flows) {
        this.successions = succs != null ? succs : new ArrayList<>();
        this.flows = flows != null ? flows : new ArrayList<>();
    }

    public SysMLV2Configuration clone() {
        return new SysMLV2Configuration(
            new ArrayList<>(this.successions),
            new ArrayList<>(this.flows)
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SysMLV2Configuration other = (SysMLV2Configuration) obj;
        
        // Verifica tamanho (nós ativos de controle e de objeto/dado)
        if (this.successions.size() != other.successions.size()) return false;
        if (this.flows.size() != other.flows.size()) return false;
        
        // Compara os IDs de Successions
        List<String> thisSuccIds = this.successions.stream().map(s -> s.getID()).sorted().toList();
        List<String> otherSuccIds = other.successions.stream().map(s -> s.getID()).sorted().toList();
        
        // Compara os IDs de Flows
        List<String> thisFlowIds = this.flows.stream().map(f -> f.getID()).sorted().toList();
        List<String> otherFlowIds = other.flows.stream().map(f -> f.getID()).sorted().toList();
        
        return thisSuccIds.equals(otherSuccIds) && thisFlowIds.equals(otherFlowIds);
    }

    @Override
    public int hashCode() {
        int result = successions.stream().map(s -> s.getID()).sorted().toList().hashCode();
        result = 31 * result + flows.stream().map(f -> f.getID()).sorted().toList().hashCode();
        return result;
    }

    @Override
    public String toString() {
        List<String> succIds = successions.stream().map(s -> s.getID()).toList();
        List<String> flowIds = flows.stream().map(f -> {
            String payloadName = f.getPayload() != null ? f.getPayload().getDeclaredName() : "NoPayload";
            return f.getID() + "[" + payloadName + "]";
        }).toList();
        
        return "State{Successions: " + succIds + ", Flows: " + flowIds + "}";
    }
    
    public void removeSuccession(String id) {
        successions.removeIf(s -> s.getID().equals(id));
    }

    // Novo método para remoção de flows pelo ID
    public void removeFlow(String id) {
        flows.removeIf(f -> f.getID().equals(id));
    }
}