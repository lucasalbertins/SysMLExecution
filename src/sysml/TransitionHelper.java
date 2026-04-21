package sysml;

import org.omg.sysml.lang.sysml.*;

// OUTDATED
public class TransitionHelper {

    public static void printTransition(TransitionUsage transition, String indent) {
        System.out.println(indent + "Transition from: " +
                (transition.getSource() != null ? transition.getSource().getName() : "Unnamed") + " to: " +
                (transition.getTarget() != null ? transition.getTarget().getName() : "Unnamed"));
        
        //FeaturePrinter.printFeatures(transition, indent); //debug
        for (Feature feature : transition.getOwnedFeature()) {
            String featureName = feature.getName();
            System.out.println(indent + "    Feature: " + featureName);
            
            if ("guard".equals(featureName)) {
                printGuard(feature, indent + "    ");
            } else if ("effect".equals(featureName)) {
                printEffect(feature, indent + "    ");
            } else if ("accepter".equals(featureName)) {
            	ActionUsage acceptAction = (ActionUsage) feature;
                ActionProcessor actionProcessor = new ActionProcessor();
                actionProcessor.processAction(acceptAction, indent );
            }
        }
    }

    private static void printGuard(Feature feature, String indent) {
        System.out.println(indent + "Guard (if condition):");
        // operador
        if (feature instanceof OperatorExpression) {
            OperatorExpression guardExpr = (OperatorExpression) feature;
            System.out.println(indent + "  Operator: " + guardExpr.getOperator());

            // recursao
            for (Expression operand : guardExpr.getOperand()) {
                printGuardExpression(operand, indent + "    ");
            }

        // expressão em cadeia (FeatureChainExpression)
        } else if (feature instanceof FeatureChainExpression) {
            FeatureChainExpression chainExpr = (FeatureChainExpression) feature;
            System.out.println(indent + "  Guard as FeatureChainExpression:");

            for (Feature chainedFeature : chainExpr.getFeature()) {
                System.out.println(indent + "    Chained Feature: " + chainedFeature.getName());
            }

        // Lida com outros tipos de sub-features
        } else {
            for (Feature subFeature : feature.getOwnedFeature()) {
                System.out.println(indent + "  Guard feature: " + subFeature.namingFeature().effectiveName());
            }
        }
    }

    // Função recursiva para explorar subexpressões de guarda
    private static void printGuardExpression(Expression expression, String indent) {
        if (expression instanceof FeatureChainExpression) {
            FeatureChainExpression featChain = (FeatureChainExpression) expression;
            System.out.println(indent + "FeatureChainExpression:");
            System.out.println(indent + "  Left: " + ((FeatureReferenceExpression) featChain.getOperand().getFirst()).getReferent().effectiveName());
            System.out.println(indent + "  Right: " + featChain.getTargetFeature().effectiveName());
            System.out.println(indent + "  Operator: " + featChain.getOperator());

        } else if (expression instanceof FeatureReferenceExpression) {
            FeatureReferenceExpression refExpr = (FeatureReferenceExpression) expression;
            System.out.println(indent + "Referenced Feature: " + refExpr.getReferent().getShortName());

        } else if (expression instanceof OperatorExpression) {
            OperatorExpression opExpr = (OperatorExpression) expression;
            System.out.println(indent + "OperatorExpression with operator: " + opExpr.getOperator());

            // recursao
            for (Expression operand : opExpr.getOperand()) {
                printGuardExpression(operand, indent + "    ");
            }
        }
    }
    private static void printEffect(Feature feature, String indent) {
        System.out.println(indent + "Effect (do action):");

        if (feature instanceof ActionUsage) {
            ActionUsage effectAction = (ActionUsage) feature;
            System.out.println(indent + "  Effect Action Usage:" + effectAction.effectiveName());

            // ActionProcessor para identificar e processar a ação
            ActionProcessor actionProcessor = new ActionProcessor();
            actionProcessor.processAction(effectAction, indent + "    ");
        } else {
            System.out.println(indent + "  Unknown effect type or no effect action found");
        }
    }
}
