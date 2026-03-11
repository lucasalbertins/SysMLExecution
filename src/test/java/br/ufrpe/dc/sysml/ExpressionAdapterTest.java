package br.ufrpe.dc.sysml;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.*;

import adapters.structures.expressions.ExpressionAdapter;
import adapters.structures.expressions.LiteralExpressionAdapter;

class ExpressionAdapterTest {

    static private SysMLV2Spec sysmlSpec;
    static private Namespace rootNamespace;

    @BeforeAll
    static void setup() {
        sysmlSpec = new SysMLV2Spec();
        sysmlSpec.parseFile("UnitsExample.sysml");
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace);
    }

    @Test
    @DisplayName("Test Literal Integer Expression")
    void testLiteralIntegerExpression() {
        AttributeUsage mass = findAttributeUsage("mass");
        FeatureValue fv = getFeatureValue(mass);
        Expression expr = (Expression) fv.getOwnedMemberElement();

        ExpressionAdapter adapter = ExpressionAdapter.of(expr);
        assertEquals("OperatorExpression", adapter.getType());

        // O primeiro argumento é o valor
        Expression valueExpr = ((OperatorExpression) expr).getArgument().get(0);
        ExpressionAdapter valueAdapter = ExpressionAdapter.of(valueExpr);
        assertTrue(valueAdapter instanceof LiteralExpressionAdapter);
        assertEquals("1350", ((LiteralExpressionAdapter) valueAdapter).asLiteral());
    }

    @Test
    @DisplayName("Test Unit Extraction from Operator Expression")
    void testUnitExtractionFromOperatorExpression() {
        AttributeUsage mass = findAttributeUsage("mass");
        FeatureValue fv = getFeatureValue(mass);
        Expression expr = (Expression) fv.getOwnedMemberElement();

        assertTrue(expr instanceof OperatorExpression);
        OperatorExpression op = (OperatorExpression) expr;

        // O segundo argumento é a unidade
        Expression unitExpr = op.getArgument().get(1);
        assertTrue(unitExpr instanceof FeatureReferenceExpression);

        FeatureReferenceExpression ref = (FeatureReferenceExpression) unitExpr;
        assertEquals("kg", ref.getReferent().getShortName());
    }

    // --- Métodos auxiliares ---

    private AttributeUsage findAttributeUsage(String name) {
        for (Element e : rootNamespace.getOwnedMember()) {
            if (e instanceof PartUsage && e.getDeclaredName().equals("vehicle1")) {
                for (Element f : ((PartUsage) e).getOwnedMember()) {
                    if (f instanceof AttributeUsage && name.equals(f.getDeclaredName())) {
                        return (AttributeUsage) f;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Attribute " + name + " not found");
    }

    private FeatureValue getFeatureValue(AttributeUsage attr) {
        return attr.getOwnedMembership().stream()
                   .filter(f -> f instanceof FeatureValue)
                   .map(f -> (FeatureValue) f)
                   .findFirst()
                   .orElseThrow(() -> new IllegalStateException("No FeatureValue found"));
    }
}