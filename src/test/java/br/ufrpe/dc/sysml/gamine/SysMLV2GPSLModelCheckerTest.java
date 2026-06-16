package br.ufrpe.dc.sysml.gamine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Namespace;

import adapters.behavior.actions.ActionUsageAdapter;
import adapters.behavior.actions.ActionUsageAdapterRegistry;
import br.ufrpe.dc.sysml.SysMLV2Spec;
import gamine.SysMLV2GPSLModelChecker;

@DisplayName("GPSL Model Checking Specifications")
public class SysMLV2GPSLModelCheckerTest {
    
    private Namespace rootNamespace;
    private ActionUsageAdapterRegistry registry;
    
    @BeforeEach
    void init() {
        var spec = new SysMLV2Spec();
        spec.parseFile("control/NewDecisionNodeExample.sysml");
        rootNamespace = (Namespace) spec.getRootNamespace();
        registry = new ActionUsageAdapterRegistry(rootNamespace);
        System.out.println("\n===== New SysML AST loaded for GPSL testing =====");
    }

    private SysMLV2GPSLModelChecker gpslChecker(String action) {
        ActionUsageAdapter ua = registry.getByDeclaredName(action).getFirst();
        return new SysMLV2GPSLModelChecker(ua, rootNamespace);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. SAFETY PROPERTIES: "Algo ruim NUNCA acontece"
    // Operadores: [] (Sempre) ou G (Globally)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Safety Properties (Globally / [])")
    class SafetyProperties {

        @Test
        @DisplayName("A bateria nunca deve ficar negativa")
        void batteryAlwaysPositive() {
            var result = gpslChecker("chargeBattery").check("p =! [] |battery >= 0.0|");
            assertTrue(result.holds(), "A bateria não pode ter carga negativa.");
        }

        @Test
        @DisplayName("A bateria não deve passar de 110.0 (Contraexemplo esperado se limite for 100)")
        void batteryNeverExceedsStrictLimit() {
            // Se o seu modelo de fato vai até 110, testar que é SEMPRE <= 100 vai falhar.
            var result = gpslChecker("chargeBattery").check("p =! G |battery <= 100.0|");
            assertFalse(result.holds(), "A propriedade deve falhar, pois o modelo atinge 110.0");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. LIVENESS PROPERTIES: "Algo bom EVENTUALMENTE acontece"
    // Operadores: <> (Eventualmente) ou F (Future)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Liveness Properties (Eventually / <>)")
    class LivenessProperties {

        @Test
        @DisplayName("O sistema deve terminar com sucesso (atingir o nó done)")
        void systemTerminates() {
            var result = gpslChecker("chargeBattery").check("p =! <> |done|");
            assertTrue(result.holds(), "O sistema não pode entrar em Livelock/Deadlock, deve terminar.");
        }

        @Test
        @DisplayName("A bateria eventualmente atinge a carga total")
        void batteryEventuallyFull() {
            // F (Future) avaliando uma expressão condicional composta no SpEL
            var result = gpslChecker("chargeBattery").check("p =! F (|battery >= 100.0| && |isCharging == false|)");
            assertTrue(result.holds(), "O sistema deve chegar a um estado onde a bateria está cheia e não está mais carregando.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. CAUSALITY & UNTIL PROPERTIES: Condições ao longo do tempo
    // Operadores: W (Weak Until), U (Strong Until), -> (Implies)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Causality Properties (Until / Implies)")
    class CausalityProperties {

        @Test
        @DisplayName("A bateria permanece <= 100 até que o carregamento termine")
        void batteryStateUntilChargingStops() {
            // W (Weak Until): p se mantém verdadeiro até que q seja verdadeiro
            var result = gpslChecker("chargeBattery").check("p =! |battery <= 100.0| W |isCharging == false|");
            assertTrue(result.holds(), "A bateria deve respeitar o limite ATÉ o carregamento ser desligado.");
        }

        @Test
        @DisplayName("Se a bateria chegar em 110.0, implica que eventualmente o carregamento será desligado")
        void implicationTest() {
            // [] (p -> <> q) : Sempre que p for verdade, q deve ser verdade no futuro (ou no mesmo instante)
            var result = gpslChecker("chargeBattery").check("p =! [] (|battery == 110.0| -> <> |isCharging == false|)");
            
            // Note o '-> <>' na string. Isso permite que o SysML caminhe pelos nós de Merge e Decision 
            // antes de finalmente alcançar o nó 'endCharging'.
            assertTrue(result.holds(), "Ao atingir 110.0, o fluxo deve obrigatoriamente levar ao desligamento.");
        }
    }
}