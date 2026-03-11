package adapters.structures.expressions;

import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.LiteralBoolean;
import org.omg.sysml.lang.sysml.LiteralInfinity;
import org.omg.sysml.lang.sysml.LiteralInteger;
import org.omg.sysml.lang.sysml.LiteralRational;
import org.omg.sysml.lang.sysml.LiteralString;

import interfaces.structures.expressions.ILiteralExpression;

//Adapter para LiteralExpressions (booleano, inteiro, real, string, infinito)
public class LiteralExpressionAdapter extends ExpressionAdapter implements ILiteralExpression {
	 LiteralExpressionAdapter(Expression literal) {
	     super(literal);
	 }

	 public String asLiteral() {
	     // descobre qual sub‑tipo e extrai seu valor
	     if (expr instanceof LiteralInteger) {
	         return Integer.toString(((LiteralInteger) expr).getValue());
	     } else if (expr instanceof LiteralRational) {
	         return Double.toString(((LiteralRational) expr).getValue());
	     } else if (expr instanceof LiteralString) {
	         return ((LiteralString) expr).getValue();
	     } else if (expr instanceof LiteralBoolean) {
	         return Boolean.toString(((LiteralBoolean) expr).isValue());
	     } else if (expr instanceof LiteralInfinity) {
	         return "*";
	     }
	     return null;
	 }

	@Override
	public String getValue() {
		return null;
		
	}

	@Override
	public String getLiteralType() {
		return null;
	}
}