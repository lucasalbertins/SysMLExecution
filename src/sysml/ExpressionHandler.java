package sysml;

import java.util.Iterator;
import java.util.List;

import org.omg.sysml.lang.sysml.*;
import org.omg.sysml.util.ExpressionUtil;

public class ExpressionHandler {

	// Lidar com diferentes Expression's
	public static void processExpression(Expression expression, String indent) {
		if (expression instanceof FeatureChainExpression) {
			FeatureChainExpression featChain = (FeatureChainExpression) expression;
			System.out.println(indent + "FeatureChainExpression:");
			System.out.println(indent + "  Left: "
					+ ((FeatureReferenceExpression) featChain.getOperand().getFirst()).getReferent().getName());
			System.out.println(indent + "  Right: " + featChain.getTargetFeature().getName());
		} else if (expression instanceof FeatureReferenceExpression) {
			FeatureReferenceExpression refExpr = (FeatureReferenceExpression) expression;
			System.out.println(indent + "Referenced Feature: "
					+ (refExpr.getReferent() != null ? refExpr.getReferent().getName() : "Unnamed"));
		} else if (expression instanceof LiteralString) {
			LiteralString literal = (LiteralString) expression;
			System.out.println(indent + "Literal String: " + literal.getValue());
		} else if (expression instanceof LiteralInteger) {
			LiteralInteger literal = (LiteralInteger) expression;
			System.out.println(indent + "Literal Integer: " + literal.getValue());
		} else {
			System.out.println(indent + "Unknown expression type: " + expression.getClass().getSimpleName());
		}
	}

	// Payload Argument - WIP
	public static String processPayload(Expression payloadArgument) {
	    if (payloadArgument instanceof InvocationExpression) {
	        InvocationExpression invocation = (InvocationExpression) payloadArgument;

	        // Processar os argumentos
	        List<Expression> arguments = invocation.getArgument();
	        if (arguments != null && !arguments.isEmpty()) {
	            for (Expression expression : arguments) {
	                if (expression instanceof LiteralBoolean) {
	                    LiteralBoolean booleanLiteral = (LiteralBoolean) expression;

	                    // Verificar as features para obter o nome
	                    for (Feature feature : invocation.getOwnedFeature()) {
	                        String featureName = feature.getName();
	                        if (featureName != null) {
	                            return featureName + ": " + booleanLiteral.isValue();
	                        }
	                    }

	                    // Caso não encontre nas features
	                    return "UnnamedFeature: " + booleanLiteral.isValue();
	                } else if (expression instanceof LiteralString) {
	                    LiteralString stringLiteral = (LiteralString) expression;
	                    return "LiteralString: " + stringLiteral.getValue();
	                } else if (expression instanceof LiteralInteger) {
	                    LiteralInteger integerLiteral = (LiteralInteger) expression;
	                    return "LiteralInteger: " + integerLiteral.getValue();
	                }
	            }
	        }
	    }
	    return "DefaultPayload"; // Fallback
	}



	// Receiver Argument 
	public static String processReceiver(Expression receiverArgument) {
		if (receiverArgument instanceof FeatureReferenceExpression) {
			FeatureReferenceExpression featureRef = (FeatureReferenceExpression) receiverArgument;
			Feature referent = featureRef.getReferent();
			return referent != null ? referent.getName() : "UnnamedReceiver";
		}
		return "UnnamedReceiver";
	}

	// Sender Argument
	public static String processSender(Expression senderArgument) {
		if (senderArgument instanceof FeatureReferenceExpression) {
			FeatureReferenceExpression featureRef = (FeatureReferenceExpression) senderArgument;
			Feature referent = featureRef.getReferent();
			return referent != null ? referent.getName() : "DefaultSender";
		}
		return "DefaultSender";
	}
}
