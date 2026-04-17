package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.omg.sysml.interactive.SysMLInteractive;

// OUTDATED
public class ExpressionEvaluationTest extends SysMLInteractiveTest {

	protected void assertElement(String message, String expected, String actual) {
		message = message == null? "": message + " ";
		assertTrue(actual.startsWith(expected), message + "expected: " + expected + " actual: " + actual);
	}
	
	protected void assertElement(String expected, String actual) {
		assertElement(null, expected, actual);
	}
	
	protected void assertList(String[] expecteds, String actual) {
		String[] actuals = actual.isEmpty()? new String[] {}: actual.split("\n");
		assertEquals(expecteds.length, actuals.length, "length");
		for (int i = 0; i < expecteds.length; i++) {
			assertElement("[" + i + "]", expecteds[i], actuals[i]);
		}
	}
	
	public final String evalTest1 =
			"package ActionFlow {\n"
			+ " part def PartDef;\n"
			+ " action def Action1;\n"
			+ " action def Action2;\n"
			+ " action def Action3;\n"
			+ " action def Action4;\n"
			+ " part Part :> PartDef {\n"
			+ "		action def Action0 {\n"
			+ "		action action1 :> Action1;\n"
			+ "		action action2 :> Action2;\n"
			+ "		action action3 :> Action3;\n"
			+ "		action action4 :> Action4;\n"
			+ " }\n"
			+ "	perform action action0 :> Action0;\n"
			+ "	}\n"
			+ "}";
	
	@Test
	public void testEvaluation1() throws Exception {
		SysMLInteractive instance = getSysMLInteractiveInstance();
		process(instance, evalTest1);
		//assertElement("LiteralInteger 3", instance.eval("p11.a2", "EvalTest1"));
		//assertElement("LiteralInteger 2", instance.eval("p11.p11.a2", "EvalTest1"));
		//assertElement("LiteralInteger 2", instance.eval("p11.p12.a2", "EvalTest1"));
		//assertElement("LiteralInteger 2", instance.eval("p1.p11.a2", "EvalTest1"));
	}
}