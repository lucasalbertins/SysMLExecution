package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.AttributeUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;
import org.omg.sysml.lang.sysml.LiteralInteger;
import org.omg.sysml.lang.sysml.Membership;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.OperatorExpression;
import org.omg.sysml.lang.sysml.PartDefinition;
import org.omg.sysml.lang.sysml.PartUsage;
import org.omg.sysml.lang.sysml.FeatureValue;

// OUTDATED
public class UnitExampleTest {

    static private SysMLV2Spec sysmlSpec;
    static private Namespace rootNamespace;
    static private PartDefinition vehiclePart;
    static private PartUsage testVehicle;

    @BeforeAll
    static void setUp() {
        // Instancia SysMLV2Spec usando o construtor que recebe os caminhos fixos (para testes)
        sysmlSpec = new SysMLV2Spec();
        // Carrega o arquivo UnitsExample.sysml
        sysmlSpec.parseFile("UnitsExample.sysml");

        // Obtém o namespace raiz
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "O namespace raiz não deve ser nulo.");

        // Busca a part definition "Vehicle"
        vehiclePart = findPartDefinitionByName(rootNamespace, "Vehicle");
        assertNotNull(vehiclePart, "A part 'Vehicle' não foi encontrada no modelo.");

        // Busca a part usage "vehicle1"
        testVehicle = findPartUsageByName(rootNamespace, "vehicle1").orElse(null);
        assertNotNull(testVehicle, "A part usage 'vehicle1' não foi encontrada no modelo.");
    }

    @Test
    void testMassValueAndUnit() {
        // Busca recursivamente o atributo "mass" dentro do uso da part "vehicle1"
        Optional<AttributeUsage> optMassAttr = findAttributeUsageRecursively(testVehicle, "mass");
        assertTrue(optMassAttr.isPresent(), "O atributo 'mass' deve estar presente em 'vehicle1'.");
        AttributeUsage massAttr = optMassAttr.get();
        
        // Percorre os memberships para encontrar a FeatureValue correspondente
        boolean foundFeatureValue = false;
        for (Membership membership : massAttr.getOwnedMembership()) {
            if (membership instanceof FeatureValue) {
                FeatureValue fv = (FeatureValue) membership;
                // A expressão associada deve ser OperatorExpression
                OperatorExpression expr = (OperatorExpression) fv.getOwnedMemberElement();
                assertNotNull(expr, "A expressão da FeatureValue não pode ser nula.");
                List<?> args = expr.getArgument();
                assertFalse(args.isEmpty(), "Os argumentos da expressão não podem ser vazios.");

                // O primeiro argumento deve ser um LiteralInteger com valor 1350
                LiteralInteger literal = (LiteralInteger) args.get(0);
                int value = literal.getValue();
                assertEquals(1350, value, "O valor de 'mass' deve ser 1350.");

                // O segundo argumento deve ser uma FeatureReferenceExpression para a unidade
                FeatureReferenceExpression fre = (FeatureReferenceExpression) args.get(1);
                String unit = fre.getReferent().getShortName();
                assertTrue(unit.equals("kg") || unit.equals("SI::kg"), "A unidade deve ser 'kg' ou 'SI::kg'.");
                foundFeatureValue = true;
                break;
            }
        }
        assertTrue(foundFeatureValue, "Não foi encontrada a FeatureValue associada ao atributo 'mass'.");
    }

    // Métodos auxiliares

    private static PartDefinition findPartDefinitionByName(Namespace namespace, String targetName) {
        for (Element element : namespace.getOwnedMember()) {
            if (element instanceof PartDefinition && targetName.equalsIgnoreCase(element.getDeclaredName())) {
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

    private static Optional<PartUsage> findPartUsageByName(Namespace namespace, String targetName) {
        for (Element element : namespace.getOwnedMember()) {
            if (element instanceof PartUsage && targetName.equalsIgnoreCase(element.getDeclaredName())) {
                return Optional.of((PartUsage) element);
            }
            if (element instanceof Namespace) {
                Optional<PartUsage> found = findPartUsageByName((Namespace) element, targetName);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }

    // Busca recursivamente uma AttributeUsage pelo nome, testando tanto getDeclaredName() quanto getName()
    private static Optional<AttributeUsage> findAttributeUsageRecursively(Element element, String attrName) {
        if (element instanceof AttributeUsage) {
            String declared = element.getDeclaredName();
            String nome = element.getName();
            if ((declared != null && declared.equalsIgnoreCase(attrName)) ||
                (nome != null && nome.equalsIgnoreCase(attrName))) {
                return Optional.of((AttributeUsage) element);
            }
        }
        if (element instanceof Namespace) {
            for (Element child : ((Namespace) element).getOwnedMember()) {
                Optional<AttributeUsage> found = findAttributeUsageRecursively(child, attrName);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }
}
