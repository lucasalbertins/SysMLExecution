package br.ufrpe.dc.sysml;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.*;

import adapters.structures.parts.PartDefinitionAdapter;
import adapters.structures.parts.PartUsageAdapter;


class PartAdaptersTest {
    private static SysMLV2Spec sysmlSpec;
    private static Namespace rootNamespace;

    @BeforeAll
    static void init() {
        sysmlSpec = new SysMLV2Spec();
        // ajuste o caminho do arquivo se necessário
        sysmlSpec.parseFile("control/FlowUsageExample.sysml");
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "rootNamespace não pode ser nulo");
    }

    @Test
    void testPartDefinitionAdapterAndPartUsageSpecialization() {
        // 1) encontra PartDefinition "Vehicle"
        PartDefinition pd = findPartDefinitionByName(rootNamespace, "Vehicle");
        assertNotNull(pd, "PartDefinition Vehicle deve existir no modelo");

        // 2) cria adapter e imprime dados
        PartDefinitionAdapter adapter = new PartDefinitionAdapter(pd);
        System.out.println("Adapter.toString(): " + adapter.toString());

        String name = adapter.getName();
        System.out.println("getName(): " + name);

        List<String> ownedFeatures = adapter.getOwnedFeatures();
        System.out.println("OwnedFeatures: " + ownedFeatures);

        List<String> ownedPartUsages = adapter.getOwnedPartUsages();
        System.out.println("OwnedPartUsages (names): " + ownedPartUsages);

        // asserts básicos
        assertNotNull(name);
        assertFalse(ownedFeatures == null, "ownedFeatures não deve ser null");
        assertFalse(ownedPartUsages == null, "ownedPartUsages não deve ser null");

        // Esperamos que vehicle contenha tankAssy e eng (conforme o modelo fornecido)
        assertTrue(ownedPartUsages.contains("tankAssy"), "esperado 'tankAssy' em ownedPartUsages");
        assertTrue(ownedPartUsages.contains("eng"), "esperado 'eng' em ownedPartUsages");

        // 3) para cada part usage name, procura o PartUsage real e cria PartUsageAdapter
        for (String puName : ownedPartUsages) {
            PartUsage pu = findPartUsageByName(rootNamespace, puName);
            assertNotNull(pu, "PartUsage '" + puName + "' deve existir no modelo (encontrado via busca)");
            PartUsageAdapter puAdapter = new PartUsageAdapter(pu);
            System.out.printf("  PartUsage: %s -> specialization: %s%n", puAdapter.getName(), puAdapter.getSpecialization());

            // asserts esperados para os nomes do seu modelo (ajuste se necessário)
            if ("eng".equals(puName)) {
                assertEquals("Engine", puAdapter.getSpecialization(), "eng deve especializar Engine");
            } else if ("tankAssy".equals(puName)) {
                assertEquals("FuelTankAssembly", puAdapter.getSpecialization(), "tankAssy deve especializar FuelTankAssembly");
            }
        }
    }

    // procura recursiva de PartDefinition por nome (declaredName ou name)
    private PartDefinition findPartDefinitionByName(Element root, String name) {
        if (root == null) return null;
        if (root instanceof PartDefinition pd) {
            String n = pd.getDeclaredName() != null ? pd.getDeclaredName() : pd.getName();
            if (name.equals(n)) return pd;
        }
        if (root instanceof Namespace ns) {
            for (Element child : ns.getOwnedMember()) {
                PartDefinition res = findPartDefinitionByName(child, name);
                if (res != null) return res;
            }
        }
        return null;
    }

    // procura recursiva de PartUsage por nome (declaredName ou name)
    private PartUsage findPartUsageByName(Element root, String name) {
        if (root == null) return null;
        if (root instanceof PartUsage pu) {
            String n = pu.getDeclaredName() != null ? pu.getDeclaredName() : pu.getName();
            if (name.equals(n)) return pu;
        }
        if (root instanceof Namespace ns) {
            for (Element child : ns.getOwnedMember()) {
                PartUsage res = findPartUsageByName(child, name);
                if (res != null) return res;
            }
        }
        // também procurar dentro de Features/Usages que possam conter PartUsages como members:
        try {
            if (root instanceof org.omg.sysml.lang.sysml.Feature f) {
                for (var of : f.getOwnedFeature()) {
                    PartUsage res = findPartUsageByName(of, name);
                    if (res != null) return res;
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}