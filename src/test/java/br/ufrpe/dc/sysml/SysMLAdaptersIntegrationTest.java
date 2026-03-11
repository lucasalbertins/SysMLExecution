//package tests;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import sysml.SysMLV2Spec;
//import org.omg.sysml.lang.sysml.Element;
//import org.omg.sysml.lang.sysml.Namespace;
//import org.omg.sysml.lang.sysml.StateUsage;
//import adapters.states.StateUsageAdapter;
//
//class SysMLAdaptersIntegrationTest {
//
//    private static SysMLV2Spec spec;
//    private static Namespace rootNamespace;
//
//    @BeforeAll
//    static void setUp() {
//        // Utilize a variável de ambiente FILE_PATH ou um caminho fixo para o arquivo de teste.
//        spec = new SysMLV2Spec();
//        spec.load();
//        Element root = spec.getRootElement();
//        assertNotNull(root, "O elemento raiz não deve ser nulo.");
//        assertTrue(root instanceof Namespace, "O elemento raiz deve ser um Namespace.");
//        rootNamespace = (Namespace) root;
//    }
//
//    @Test
//    void testFindStateUsageAndAdapter() {
//        // Obter os elementos do namespace raiz
//        List<Element> elements = rootNamespace.getOwnedMember();
//        // Filtrar por um elemento que seja do tipo StateUsage
//        Optional<Element> optStateUsage = elements.stream()
//            .filter(e -> e instanceof StateUsage)
//            .findFirst();
//        assertTrue(optStateUsage.isPresent(), "O modelo deve conter pelo menos um StateUsage.");
//
//        // Instanciar o adaptador para o StateUsage encontrado
//        StateUsage stateUsage = (StateUsage) optStateUsage.get();
//        StateUsageAdapter adapter = new StateUsageAdapter(stateUsage);
//        String stateName = adapter.getName();
//        assertNotNull(stateName, "O nome retornado pelo StateUsageAdapter não deve ser nulo.");
//        // Opcional: verificar se o nome corresponde ao esperado (por exemplo, \"BatteryStates\")\n        // assertEquals(\"BatteryStates\", stateName);\n    }\n}\n"
//    }
//}
