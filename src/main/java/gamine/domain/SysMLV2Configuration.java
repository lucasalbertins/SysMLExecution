package gamine.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interfaces.behavior.actions.IActionDefinition;
import interfaces.behavior.actions.IActionUsage;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.IFlow;

public class SysMLV2Configuration {

    public List<ISuccession> successions;
    public List<IFlow> flows;
    public Map<String, Object> memory;
    
    // NEW: Track active subactions and their definitions
    public Map<String, IActionDefinition> activeSubactions;  // actionID -> ActionDefinition
    public Map<String, IActionUsage> subactionUsages;        // actionID -> ActionUsage

    public SysMLV2Configuration(List<ISuccession> succs, List<IFlow> flows) {
        this(succs, flows, new HashMap<>());
    }

    public SysMLV2Configuration(List<ISuccession> succs, List<IFlow> flows, Map<String, Object> memory) {
        this(succs, flows, memory, new HashMap<>(), new HashMap<>());
    }
    
    // NEW: Full constructor with subaction tracking
    public SysMLV2Configuration(List<ISuccession> succs, List<IFlow> flows, 
                                Map<String, Object> memory,
                                Map<String, IActionDefinition> activeSubactions,
                                Map<String, IActionUsage> subactionUsages) {
        this.successions = succs != null ? succs : new ArrayList<>();
        this.flows = flows != null ? flows : new ArrayList<>();
        this.memory = memory != null ? memory : new HashMap<>();
        this.activeSubactions = activeSubactions != null ? activeSubactions : new HashMap<>();
        this.subactionUsages = subactionUsages != null ? subactionUsages : new HashMap<>();
    }

    public SysMLV2Configuration clone() {
        return new SysMLV2Configuration(
            new ArrayList<>(this.successions),
            new ArrayList<>(this.flows),
            new HashMap<>(this.memory),
            new HashMap<>(this.activeSubactions),
            new HashMap<>(this.subactionUsages)
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SysMLV2Configuration other = (SysMLV2Configuration) obj;
        
        if (this.successions.size() != other.successions.size()) return false;
        if (this.flows.size() != other.flows.size()) return false;
        if (this.activeSubactions.size() != other.activeSubactions.size()) return false;
        
        List<String> thisSuccIds = this.successions.stream().map(s -> s.getID()).sorted().toList();
        List<String> otherSuccIds = other.successions.stream().map(s -> s.getID()).sorted().toList();
        
        List<String> thisFlowIds = this.flows.stream().map(f -> f.getID()).sorted().toList();
        List<String> otherFlowIds = other.flows.stream().map(f -> f.getID()).sorted().toList();
        
        return thisSuccIds.equals(otherSuccIds) 
            && thisFlowIds.equals(otherFlowIds)
            && this.memory.equals(other.memory)
            && this.activeSubactions.keySet().equals(other.activeSubactions.keySet());
    }

    @Override
    public int hashCode() {
        int result = successions.stream().map(s -> s.getID()).sorted().toList().hashCode();
        result = 31 * result + flows.stream().map(f -> f.getID()).sorted().toList().hashCode();
        result = 31 * result + memory.hashCode();
        result = 31 * result + activeSubactions.keySet().hashCode();
        return result;
    }

    @Override
    public String toString() {
        List<String> succIds = successions.stream().map(s -> s.getID()).toList();
        List<String> flowIds = flows.stream().map(f -> {
            String payloadName = f.getPayload() != null ? f.getPayload().getDeclaredName() : "NoPayload";
            return f.getID() + "[" + payloadName + "]";
        }).toList();
        List<String> subactionIds = new ArrayList<>(activeSubactions.keySet());
        
        return "State{Successions: " + succIds + ", Flows: " + flowIds + ", Memory: " + memory 
               + ", ActiveSubactions: " + subactionIds + "}";
    }
    
    public void removeSuccession(String id) {
        successions.removeIf(s -> s.getID().equals(id));
    }
    
    public void removeFlow(String id) {
        flows.removeIf(f -> f.getID().equals(id));
    }
    
    // NEW: Subaction management
    public void registerSubaction(String actionID, IActionUsage usage, IActionDefinition definition) {
        this.subactionUsages.put(actionID, usage);
        this.activeSubactions.put(actionID, definition);
    }
    
    public void unregisterSubaction(String actionID) {
        this.activeSubactions.remove(actionID);
        this.subactionUsages.remove(actionID);
    }
    
    public IActionDefinition getSubactionDefinition(String actionID) {
        return this.activeSubactions.get(actionID);
    }
    
    public IActionUsage getSubactionUsage(String actionID) {
        return this.subactionUsages.get(actionID);
    }
}