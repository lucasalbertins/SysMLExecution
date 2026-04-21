package sysml;

import org.omg.sysml.lang.sysml.*;

// OUTDATED
public class ElementPrinter {

    public static void printElementStructure(Element element, String indent) {
        String declaredName = (element.getDeclaredName() != null) ? element.getDeclaredName() : element.getName();
        declaredName = (declaredName != null) ? declaredName : "Unnamed";

        if (element instanceof PartDefinition) {
            printPartDefinition((PartDefinition) element, indent);
        } else if (element instanceof PartUsage) {
            System.out.println(indent + "PartUsage: " + declaredName);
        } else if (element instanceof AttributeDefinition) {
            System.out.println(indent + "AttributeDefinition: " + declaredName);
        } else if (element instanceof AttributeUsage) {
            printAttributeUsage((AttributeUsage) element, indent);
        } else if (element instanceof StateUsage) {
            StateHelper.printState((StateUsage) element, indent);
        } else if (element instanceof TransitionUsage) {
            TransitionHelper.printTransition((TransitionUsage) element, indent);
        } 
        // Debug
//        else {
//            System.out.println(indent + "Other Element: " + declaredName + " " + element.eClass().getName());
//        }

        if (element instanceof Namespace) {
            for (Element member : ((Namespace) element).getOwnedMember()) {
                printElementStructure(member, indent + "  ");
            }
        }
    }

    private static void printPartDefinition(PartDefinition partDefinition, String indent) {
        System.out.println(indent + "PartDefinition: " + partDefinition.getDeclaredName());
    }

    private static void printAttributeUsage(AttributeUsage attribute, String indent) {
        System.out.println(indent + "AttributeUsage: " + attribute.getDeclaredName());
        for (Type att : attribute.getType()) {
            System.out.println(indent + "  Type: " + att.getName());
        }
    }

//    private static void printStateUsage(StateUsage state, String indent) {
//        System.out.println(indent + "StateUsage: " + state.getDeclaredName());
//        for (ActionUsage action : state.getNestedAction()) {
//            if (action instanceof AcceptActionUsage) {
//                AcceptActionUsage acceptAction = (AcceptActionUsage) action;
//                System.out.println(indent + "  AcceptActionUsage: " + acceptAction.getName());
//                Expression payloadArgument = acceptAction.getPayloadArgument();
//                System.out.println(indent + "    PayloadArgument: " + (payloadArgument != null ? payloadArgument.toString() : "None"));
//            } else if (action instanceof AssignmentActionUsage) {
//                AssignmentActionUsage assignmentAction = (AssignmentActionUsage) action;
//                System.out.println(indent + "  AssignmentActionUsage: " + assignmentAction.getName());
//                Expression targetArgument = assignmentAction.getTargetArgument(); 
//                Expression valueExpression = assignmentAction.getValueExpression();
//                if (targetArgument instanceof FeatureReferenceExpression) {
//                    Feature targetFeature = ((FeatureReferenceExpression) targetArgument).getReferent();
//                    System.out.println(indent + "    TargetArgument: " + (targetFeature != null ? targetFeature.getName() : "Unnamed"));
//                }
//                System.out.println(indent + "    ValueExpression: " + (valueExpression != null ? valueExpression.getName() : "Unnamed")); //unsure
//            }
//        }
//        for (StateUsage subState : state.getNestedState()) {
//            printElementStructure(subState, indent + "  ");
//        }
//    }
}
