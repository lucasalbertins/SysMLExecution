package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.*;

import adapters.structures.attributes.AttributeUsageAdapter;
import adapters.structures.expressions.ExpressionAdapter;

// OUTDATED
public class LiteralExpressionsExampleTest{

    private static SysMLV2Spec sysmlSpec;
    private static Namespace rootNamespace;
    private static PartDefinition vehicleDef;

    @BeforeAll
    static void setUp() {
        sysmlSpec = new SysMLV2Spec();
        sysmlSpec.parseFile("other/LiteralExpressionsExample.sysml");
        rootNamespace = sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "O namespace raiz não deve ser nulo.");

        vehicleDef = findPartDefinitionByName(rootNamespace, "Vehicle");
        assertNotNull(vehicleDef, "A PartDefinition 'Vehicle' não foi encontrada.");
    }

    @Test
    void testLiteralAttributeValues() {
        // Lista de atributos esperados e seus valores esperados
        assertAttributeValue("speed", "120.0");
        assertAttributeValue("maxLoad", "500");
        assertAttributeUnit("maxLoad", "kg");

        assertAttributeValue("modelName", "X-Series");
        assertAttributeValue("isElectric", "true");
        assertAttributeValue("serialNumber", "123456");

        // Verificação do enum (vai retornar o nome da literal)
        assertAttributeValue("vehicleColor", "Red");
    }

    // Métodos auxiliares

    private static void assertAttributeValue(String attrName, String expectedValue) {
        Optional<AttributeUsage> opt = findAttributeUsage(vehicleDef, attrName);
        assertTrue(opt.isPresent(), "Atributo '" + attrName + "' não encontrado.");
        AttributeUsageAdapter adapter = new AttributeUsageAdapter(opt.get());
        assertEquals(expectedValue, adapter.getValue(), "Valor do atributo '" + attrName + "' incorreto.");
    }

    private static void assertAttributeUnit(String attrName, String expectedUnit) {
        Optional<AttributeUsage> opt = findAttributeUsage(vehicleDef, attrName);
        assertTrue(opt.isPresent(), "Atributo '" + attrName + "' não encontrado.");
        AttributeUsageAdapter adapter = new AttributeUsageAdapter(opt.get());
        assertEquals(expectedUnit, adapter.getUnit(), "Unidade do atributo '" + attrName + "' incorreta.");
    }

    private static Optional<AttributeUsage> findAttributeUsage(Namespace ns, String attrName) {
        return ns.getOwnedMember().stream()
            .filter(AttributeUsage.class::isInstance)
            .map(AttributeUsage.class::cast)
            .filter(a -> attrName.equals(a.getDeclaredName()))
            .findFirst();
    }

    private static PartDefinition findPartDefinitionByName(Element root, String name) {
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
}
