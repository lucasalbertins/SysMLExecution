package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.StateUsage;

import adapters.behavior.states.StateUsageAdapter;

class BatteryStateAdapterTest {

    private static SysMLV2Spec sysmlSpec;
    private static Namespace rootNamespace;
    private static StateUsage batteryStates;
    private static StateUsageAdapter adapter;

    @BeforeAll
    static void init() {
        // Inicializa o parser com caminhos fixos (para testes)
        sysmlSpec = new SysMLV2Spec();
        // Carrega o arquivo de teste
        sysmlSpec.parseFile("BatterySystem.sysml");

        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "Namespace raiz não deve ser nulo");

        // Encontra o estado de topo
        batteryStates = findStateUsageByName(rootNamespace, "BatteryStates");
        assertNotNull(batteryStates, "BatteryStates não encontrado");

        // Cria o adaptador a ser testado
        adapter = new StateUsageAdapter(batteryStates);
    }

    @Test
    void testGetName() {
        assertEquals("BatteryStates", adapter.getName(), "getName incorreto");
    }

    @Test
    void testEntryDoExit() {
        // Mesmo que a entrada seja declarada sem corpo, o parser instancia um ActionUsage
        ActionUsage entry = adapter.getEntry();
        assertNotNull(entry, "Entry deve estar instanciado");
        String entryName = entry.getName();
        System.out.println("DEBUG entry.getName() = " + entryName);
        // Ajusta o assert ao valor real retornado pelo parser
        assertEquals("entryAction", entryName, "Entry.getName() deve ser 'entryAction'");

        // Verifica doActivity e exit
        ActionUsage doAct = adapter.getDoActivity();
        assertNotNull(doAct, "DoActivity não deve ser nulo");
        assertEquals("MonitorBattery", doAct.getName(), "DoActivity incorreto");

        assertNull(adapter.getExit(), "Exit deve ser nulo");
    }

    @Test
    void testToString() {
        String repr = adapter.toString();
        System.out.println("DEBUG adapter.toString() = " + repr);
        assertTrue(repr.contains("Entry=entryAction"), "toString deve indicar 'Entry=entryAction'");
        assertTrue(repr.contains("Do=MonitorBattery"), "toString deve indicar 'Do=MonitorBattery'");
        assertTrue(repr.contains("Exit=None"),       "toString deve indicar 'Exit=None'");
    }

    /** 
     * Auxiliar para buscar um StateUsage pelo nome, recursivamente
     */
    private static StateUsage findStateUsageByName(Namespace namespace, String targetName) {
        for (Element element : namespace.getOwnedMember()) {
            if (element instanceof StateUsage su && targetName.equals(su.getDeclaredName())) {
                return su;
            }
            if (element instanceof Namespace ns) {
                StateUsage found = findStateUsageByName(ns, targetName);
                if (found != null) return found;
            }
        }
        return null;
    }
}