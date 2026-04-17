package adapters.structures.expressions;

import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.LiteralBoolean;
import org.omg.sysml.lang.sysml.LiteralInteger;
import org.omg.sysml.lang.sysml.LiteralString;
import org.omg.sysml.lang.sysml.LiteralRational;
import adapters.utils.NamedElementAdapter;
import interfaces.structures.expressions.IExpression;

import org.omg.sysml.lang.sysml.FeatureChainExpression;
import org.omg.sysml.lang.sysml.LiteralInfinity;

// OUTDATED
// adapter genérico
public abstract class ExpressionAdapter extends NamedElementAdapter implements IExpression {
	protected final Expression expr;
	protected ExpressionAdapter(Expression expr) {
		super(expr);
		this.expr = expr;
 }
 
 @Override
 public String getType() {
     // exibe só o nome do tipo de expressão
     return expr.getClass().getSimpleName();
 }
 
 // fábrica estática para criar o adaptador para o caso certo 
 public static ExpressionAdapter of(Expression expr) {
     if (expr instanceof FeatureChainExpression) {
         return new FeatureChainExpressionAdapter((FeatureChainExpression) expr);
     } else if (expr instanceof LiteralBoolean
             || expr instanceof LiteralInteger
             || expr instanceof LiteralRational
             || expr instanceof LiteralString
             || expr instanceof LiteralInfinity) {
         return new LiteralExpressionAdapter(expr);
     }
     // implementar outros cases
     return new UnsupportedExpressionAdapter(expr);
     }
}

/** Fallback para expressões não suportadas ainda */
class UnsupportedExpressionAdapter extends ExpressionAdapter {
	UnsupportedExpressionAdapter(Expression expr) {
		super(expr);
}
 
 @Override
public String toString() {
	 return "UnsupportedExpression(" + getType() + ")";
}

@Override
public String getType() {
	// TODO Auto-generated method stub
	return null;
}


// public String getValue() {
//     if (expr instanceof LiteralInteger intVal) {
//         return String.valueOf(intVal.getValue());
//     }
//
//     if (expr instanceof LiteralRational realVal) {
//         return String.valueOf(realVal.getValue());
//     }
//
//     if (expr instanceof LiteralString strVal) {
//         return strVal.getValue();
//     }
//
//     // Ex: 1350 [SI::kg]
//     if (expr instanceof OperatorExpression op && "[]".equals(op.getOperator())) {
//         List<Expression> args = op.getArgument();
//         if (args.size() >= 1) {
//             return new ExpressionAdapter(args.get(0)).getValue();
//         }
//     }
//
//     return "Unknown";
// }
//
// public String getUnit() {
//     if (expr instanceof OperatorExpression op && "[]".equals(op.getOperator())) {
//         List<Expression> args = op.getArgument();
//         if (args.size() >= 2 && args.get(1) instanceof FeatureReferenceExpression ref) {
//             return ref.getReferent() != null ? ref.getReferent().getName() : "UnnamedUnit";
//         }
//     }
//     return "None";
// }
//
// public String asString() {
//     return expr.toString();
// }
//
// @Override
// public String getOperator() {
//     if (expr instanceof OperatorExpression) {
//         return ((OperatorExpression) expr).getOperator();
//     }
//     return "";
// }
// 



//    public List<IExpression> getArguments() {
//        if (expr instanceof OperatorExpression) {
//            OperatorExpression op = (OperatorExpression) expr;
//            return op.getArgument().stream()          // usa getArgument(), não getOperand()
//                     .map(arg -> new ExpressionAdapter(arg))
//                     .collect(Collectors.toList());
//        }
//        return Collections.emptyList();             // retorna lista vazia se não for OperatorExpression
//    }
}
