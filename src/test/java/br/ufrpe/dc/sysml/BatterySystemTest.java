package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.PartDefinition;
import org.omg.sysml.lang.sysml.ReferenceUsage;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.AttributeDefinition;
import org.omg.sysml.lang.sysml.AttributeUsage;
import org.omg.sysml.lang.sysml.LiteralInteger;
import org.omg.sysml.lang.sysml.DataType;

// OUTDATED
public class BatterySystemTest {

    static private SysMLV2Spec sysmlSpec;
    static private Namespace rootNamespace;
    static private PartDefinition batteryPart;

    @BeforeAll
    static void setUp() {
        // Cria a instância do parser SysMLV2Spec (nos testes, você pode passar valores fixos se necessário)
        sysmlSpec = new SysMLV2Spec();
        sysmlSpec.parseFile("BatterySystem.sysml");
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "O namespace raiz não deve ser nulo.");

        // Busca a part "Battery" no namespace (busca recursiva)
        batteryPart = findPartDefinitionByName(rootNamespace, "Battery");
        assertNotNull(batteryPart, "A part 'Battery' não foi encontrada no modelo.");
    }

    @Test
    void testAttributeUsage() {
        String[][] attributes = {
            {"minCharge", "0"},
            {"maxCharge", "100"},
            {"chargeValue", "5"},
            {"charge", "null"},
            {"sosTrigger", "null"}
        };

        for (String[] attribute : attributes) {
            String attrName = attribute[0];
            String expectedValue = attribute[1];

            Optional<AttributeUsage> optAttr = findAttributeUsageByName(batteryPart, attrName);
            assertTrue(optAttr.isPresent(), "O atributo '" + attrName + "' deve estar presente na part 'Battery'.");
            AttributeUsage attr = optAttr.get();

            Optional<LiteralInteger> optLiteral = findLiteralIntegerInElement(attr);
            if (!"null".equals(expectedValue)) {
                assertTrue(optLiteral.isPresent(), "Deve haver um LiteralInteger associado a '" + attrName + "'.");
                int value = optLiteral.get().getValue();
                assertEquals(Integer.parseInt(expectedValue), value, "O valor de '" + attrName + "' deve ser " + expectedValue + ".");
            } else {
                assertFalse(optLiteral.isPresent(), "O atributo '" + attrName + "' não deve ter um valor default.");
            }

            // Testa o tipo de dado: todos devem ser do tipo Real
            List<DataType> dataTypes = attr.getAttributeDefinition();
            assertFalse(dataTypes.isEmpty(), "O atributo '" + attrName + "' deve ter um tipo de dado definido.");
            assertEquals("Real", dataTypes.get(0).getName(), "O atributo '" + attrName + "' deve ser do tipo Real.");
        }
    }

    @Test
    void testAttributeDefinitionsExistence() {
        String[] attributeNames = {"TurnSOS", "TurnRecharge", "BatteryDepleted", "TurnOn"};

        for (String attributeName : attributeNames) {
            Optional<AttributeDefinition> optAttribute = findElementByName(rootNamespace, attributeName, AttributeDefinition.class);
            assertTrue(optAttribute.isPresent(), "O atributo '" + attributeName + "' deve estar presente no modelo.");
        }
    }

    @Test
    void testMonitorBatteryOutParameter() {
        // Procura a action MonitorBattery
        Optional<ActionUsage> optMonitorBattery = findElementByName(rootNamespace, "MonitorBattery", ActionUsage.class);
        assertTrue(optMonitorBattery.isPresent(), "O modelo deve conter a action 'MonitorBattery'.");

        ActionUsage monitorBattery = optMonitorBattery.get();
        // Procura o parâmetro 'charge' dentro de MonitorBattery
        Optional<Element> optChargeParam = monitorBattery.getOwnedMember().stream()
                .filter(e -> "charge".equals(e.getDeclaredName()))
                .findFirst();
        assertTrue(optChargeParam.isPresent(), "A action 'MonitorBattery' deve conter o parâmetro 'charge'.");

        ReferenceUsage chargeParam = (ReferenceUsage) optChargeParam.get();
        String direction = chargeParam.getDirection().toString();
        assertEquals("out", direction, "A direção do parâmetro 'charge' deve ser 'out'.");
    }

    // Métodos auxiliares para buscar elementos recursivamente

    private static PartDefinition findPartDefinitionByName(Namespace namespace, String targetName) {
        for (Element element : namespace.getOwnedMember()) {
            if (element instanceof PartDefinition && targetName.equals(element.getDeclaredName())) {
                return (PartDefinition) element;
            }
            if (element instanceof Namespace) {
                PartDefinition found = findPartDefinitionByName((Namespace) element, targetName);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static Optional<AttributeUsage> findAttributeUsageByName(PartDefinition part, String attrName) {
        List<Element> members = part.getOwnedMember();
        for (Element element : members) {
            if (element instanceof AttributeUsage && attrName.equals(element.getDeclaredName())) {
                return Optional.of((AttributeUsage) element);
            }
        }
        return Optional.empty();
    }

    private static Optional<LiteralInteger> findLiteralIntegerInElement(AttributeUsage attribute) {
        List<Element> members = attribute.getOwnedMember();
        for (Element e : members) {
            if (e instanceof LiteralInteger) {
                return Optional.of((LiteralInteger) e);
            }
        }
        return Optional.empty();
    }

    private static <T extends Element> Optional<T> findElementByName(Namespace namespace, String targetName, Class<T> clazz) {
        for (Element element : namespace.getOwnedMember()) {
            if (clazz.isInstance(element) && targetName.equals(element.getDeclaredName())) {
                return Optional.of(clazz.cast(element));
            }
            if (element instanceof Namespace) {
                Optional<T> found = findElementByName((Namespace) element, targetName, clazz);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }
}
