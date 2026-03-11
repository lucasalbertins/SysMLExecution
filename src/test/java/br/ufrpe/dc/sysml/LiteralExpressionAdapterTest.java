//package br.ufrpe.dc.sysml;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.Test;
//import org.omg.sysml.lang.sysml.LiteralInteger;
//import org.omg.sysml.lang.sysml.LiteralString;
//import org.omg.sysml.lang.sysml.impl.LiteralBooleanImpl;
//
//import adapters.expressions.ExpressionAdapter;
//import adapters.expressions.LiteralExpressionAdapter;
//
//class LiteralExpressionAdapterTest {
//
//    @Test
//    void testLiteralInteger() {
//        LiteralInteger expr = new LiteralInteger();
//        expr.setValue(42);
//
//        ExpressionAdapter adapter = ExpressionAdapter.of(expr);
//        assertEquals("42", adapter.getValue());
//    }
//
//    @Test
//    void testLiteralString() {
//        LiteralString expr = new LiteralString();
//        expr.setValue("Hello");
//
//        ExpressionAdapter adapter = ExpressionAdapter.of(expr);
//        assertEquals("Hello", adapter.getValue());
//    }
//
//    @Test
//    void testLiteralBooleanTrue() {
//        LiteralBoolean expr = new LiteralBooleanImpl();
//        expr.setValue(true);
//
//        ExpressionAdapter adapter = ExpressionAdapter.of(expr);
//        assertEquals("true", adapter.getValue());
//    }
//
//    @Test
//    void testLiteralBooleanFalse() {
//        LiteralBoolean expr = new LiteralBoolean();
//        expr.setValue(false);
//
//        ExpressionAdapter adapter = ExpressionAdapter.of(expr);
//        assertEquals("false", adapter.getValue());
//    }
//
//    @Test
//    void testLiteralRational() {
//        LiteralRational expr = new LiteralRational();
//        expr.setValue(3.14);
//
//        ExpressionAdapter adapter = ExpressionAdapter.of(expr);
//        assertEquals("3.14", adapter.getValue());
//    }
//
//    @Test
//    void testLiteralInfinity() {
//        LiteralInfinity expr = new LiteralInfinity();
//
//        ExpressionAdapter adapter = ExpressionAdapter.of(expr);
//        assertEquals("*", adapter.getValue());
//    }
//}
