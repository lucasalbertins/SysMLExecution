package adapters.behavior.states;

import java.util.List;
import java.util.stream.Collectors;

import org.omg.sysml.lang.sysml.AcceptActionUsage;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.StateUsage;
import org.omg.sysml.lang.sysml.TransitionUsage;

import adapters.behavior.actions.AcceptActionAdapter;
import interfaces.behavior.states.IStateUsage;
import interfaces.behavior.states.ITransition;

// OUTDATED
public class StateUsageAdapter implements IStateUsage {
    private StateUsage stateUsage;

    public StateUsageAdapter(StateUsage stateUsage) {
        this.stateUsage = stateUsage;
    }

    @Override
    public String getName() {
        return stateUsage.getDeclaredName();
    }

    @Override
    public ActionUsage getEntry() {
        return stateUsage.getEntryAction();
    }

    @Override
    public ActionUsage getDoActivity() {
        return stateUsage.getDoAction();
    }

    @Override
    public ActionUsage getExit() {
        return stateUsage.getExitAction();
    }
    
    @Override
    public IStateUsage[] getSubstates() {
        // Converte sub-estados do SysML para IStateUsage[]
        return null; // Implementação pendente
    }
    
    @Override
    public ITransition[] getTransitions() {
        List<ITransition> list = stateUsage.getOwnedMember().stream()
            .filter(TransitionUsage.class::isInstance)
            .map(TransitionUsage.class::cast)
            .map(TransitionAdapter::new)
            .collect(Collectors.toList());
        return list.toArray(new ITransition[0]);
    }

    /* Definir o tipo de Action com base nos metodos booleanos
    @Override
    public IActionUsage[] getAcceptActions() {
        List<IAcceptAction> list = stateUsage.getOwnedMember().stream()
            .filter(AcceptActionUsage.class::isInstance)
            .map(AcceptActionUsage.class::cast)
            .map(AcceptActionAdapter::new)
            .collect(Collectors.toList());
        return list.toArray(new IAcceptAction[0]);
    }
    */

    @Override
    public String toString() {
        return "StateUsageAdapter{" +
                "Entry=" + (getEntry() != null ? getEntry().getName() : "None") +
                ", Do=" + (getDoActivity() != null ? getDoActivity().getName() : "None") +
                ", Exit=" + (getExit() != null ? getExit().getName() : "None") +
                '}';
    }
}
