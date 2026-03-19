package gamine.domain;

import java.util.ArrayList;
import java.util.List;

import interfaces.behavior.actions.ISuccession;

public class SysMLV2Configuration {

    public List<ISuccession> successions;
    // List<IFlow>

    public SysMLV2Configuration(List<ISuccession> succs) {
        this.successions = succs;
    }

    public SysMLV2Configuration clone() {
        return new SysMLV2Configuration(new ArrayList<>(this.successions));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SysMLV2Configuration other = (SysMLV2Configuration) obj;
        
        // Verifica se possuem a mesma quantidade de tokens
        if (this.successions.size() != other.successions.size()) return false;
        
        // Compara pelos IDs das successions para garantir que representam o mesmo estado
        List<String> thisIds = this.successions.stream().map(s -> s.getID()).sorted().toList();
        List<String> otherIds = other.successions.stream().map(s -> s.getID()).sorted().toList();
        
        return thisIds.equals(otherIds);
    }

    @Override
    public int hashCode() {
        return successions.stream().map(s -> s.getID()).sorted().toList().hashCode();
    }

    @Override
    public String toString() {
        List<String> ids = successions.stream()
            .map(s -> s.getID())
            .toList();
        return "State" + ids;
    }
    
    public void removeSuccession(String id) {
        successions.removeIf(s -> s.getID().equals(id));
    }
}