package sysml;

import org.omg.sysml.lang.sysml.*;

// OUTDATED
public class AssignmentHelper {

    public static void printAssignment(AssignmentActionUsage assignment, String indent) {
        System.out.println(indent + "AssignmentActionUsage: " + assignment.getName());
        
        Expression targetArgument = assignment.getTargetArgument();
        if (targetArgument instanceof FeatureReferenceExpression) {
            Feature targetFeature = ((FeatureReferenceExpression) targetArgument).getReferent();
            System.out.println(indent + "  TargetArgument: " + (targetFeature != null ? targetFeature.getName() : "Unnamed"));
        }
        
        Expression valueExpression = assignment.getValueExpression();
        System.out.println(indent + "  ValueExpression: " + (valueExpression != null ? valueExpression.toString() : "None"));
    }
}
