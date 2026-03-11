package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.StateUsage;

import adapters.behavior.states.StateUsageAdapter;

//import java.util.List;

class StateUsageAdapterTest {
    static private SysMLV2Spec sysmlSpec;
    static private StateUsage stateUsage;
    static private StateUsageAdapter stateUsageAdapter;

    @BeforeAll
    static void setUp() {
        // Carrega o arquivo SysML
        sysmlSpec = new SysMLV2Spec();
        Namespace rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "O namespace raiz não deveria ser nulo");

        // Busca um StateUsage dentro do modelo carregado
        stateUsage = findFirstStateUsage(rootNamespace);
        assertNotNull(stateUsage, "Não foi encontrado um StateUsage no modelo.");

        // Cria o adaptador real
        stateUsageAdapter = new StateUsageAdapter(stateUsage);
    }

    @Test
    void testGetName() {
        String stateName = stateUsageAdapter.getName();
        assertNotNull(stateName, "O nome do estado não pode ser nulo.");
        System.out.println("Nome do estado: " + stateName);
    }

    @Test
    void testGetEntryAction() {
        assertNotNull(stateUsageAdapter.getEntry(), "A ação de entrada não deveria ser nula.");
    }

    @Test
    void testGetDoActivity() {
        assertNotNull(stateUsageAdapter.getDoActivity(), "A ação 'do' não deveria ser nula.");
    }

    @Test
    void testGetExitAction() {
        assertNotNull(stateUsageAdapter.getExit(), "A ação de saída não deveria ser nula.");
    }

    // Método auxiliar para encontrar o primeiro StateUsage no namespace
    private static StateUsage findFirstStateUsage(Namespace namespace) {
        for (var element : namespace.getOwnedMember()) {
            if (element instanceof StateUsage) {
                return (StateUsage) element;
            }
            if (element instanceof Namespace) {
                StateUsage found = findFirstStateUsage((Namespace) element);
                if (found != null) return found;
            }
        }
        return null;
    }
}
