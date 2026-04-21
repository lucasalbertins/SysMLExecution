package sysml;

import org.eclipse.emf.common.util.EList;
import org.omg.sysml.lang.sysml.*;
import org.omg.sysml.util.ExpressionUtil;

// OUTDATED
public class ActionProcessor {

//	EffectBehaviorUsage : ActionUsage =
//			EmptyActionUsage
//			| TransitionPerformActionUsage
//			| TransitionAcceptActionUsage
//			| TransitionSendActionUsage
//			| TransitionAssignmentActionUsage
	
    public void processAction(ActionUsage action, String indent) {
        if (action instanceof PerformActionUsage) {
            new PerformActionProcessor().process((PerformActionUsage) action, indent);
        } else if (action instanceof SendActionUsage) {
            new SendActionProcessor().process((SendActionUsage) action, indent);
        } else if (action instanceof AcceptActionUsage) {
            new AcceptActionProcessor().process((AcceptActionUsage) action, indent);
        } else if (action instanceof AssignmentActionUsage) {
            new AssignmentActionProcessor().process((AssignmentActionUsage) action, indent);
        } else {
            System.out.println(indent + "Other ActionUsage type: " + action.getClass().getSimpleName());
        }
    }

    // PerformActionUsage
    private static class PerformActionProcessor {
        public void process(PerformActionUsage action, String indent) {
            System.out.println(indent + "PerformActionUsage: " + action.getName());
        }
    }

    // SendActionUsage - WIP
    public class SendActionProcessor {
        public void process(SendActionUsage action, String indent) {
         // Argumentos do SendActionUsage
            Expression payloadArgument = action.getPayloadArgument();
            Expression receiverArgument = action.getReceiverArgument();
            Expression senderArgument = action.getSenderArgument();
            
	        //InvocationExpression invocation = (InvocationExpression) payloadArgument;
	        //System.out.println("payloadArgument: " + invocation.getArgument().getFirst().getName());
            
            // Processando os argumentos
            String payloadName = ExpressionHandler.processPayload(payloadArgument);
            String receiverName = ExpressionHandler.processReceiver(receiverArgument);
            String senderName = ExpressionHandler.processSender(senderArgument);
            // Imprimindo o resultado final
            System.out.printf("send %s() to %s%n", payloadName, receiverName);
            System.out.println("from " + senderName);
        }
    }
    
    // AcceptActionUsage
    private static class AcceptActionProcessor {
        public void process(AcceptActionUsage action, String indent) {
            System.out.println(indent + "AcceptActionUsage: ");

            for (Feature feature : action.getOwnedFeature()) {
                String featureName = feature.getName() != null ? feature.getName() : "UnnamedFeature";

                // Verifica se é o payload ou receiver
                if ("payload".equals(featureName) || "receiver".equals(featureName)) {
                    if (feature instanceof ReferenceUsage) {
                        ReferenceUsage reference = (ReferenceUsage) feature;

                        // Classifier
                        String referencedName = reference.getDefinition().isEmpty() 
                                ? "UnnamedDefinition" 
                                : reference.getDefinition().get(0).getName();
                        System.out.println(indent + "  " + featureName + ": " + referencedName);
                    }
                } else {
                    System.out.println(indent + "  Other Feature: " + featureName);
                }
            }
        }
    }

    // AssignmentActionUsage
    private static class AssignmentActionProcessor {
        public void process(AssignmentActionUsage action, String indent) {
            System.out.println(indent + "AssignmentActionUsage: " + action.getDeclaredName());
            Expression target = action.getTargetArgument();
            Expression value = action.getValueExpression();
            if (target != null) {
                System.out.println(indent + "  Target Argument: " + target.effectiveName());
            }
            if (value != null) {
                System.out.println(indent + "  Value Expression: " + value.effectiveName());
            }
        }
    }
}
