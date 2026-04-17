package adapters.structures.expressions;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.omg.sysml.lang.sysml.ConstructorExpression;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;
import org.omg.sysml.lang.sysml.InvocationExpression;
import org.omg.sysml.lang.sysml.LiteralBoolean;
import org.omg.sysml.lang.sysml.LiteralExpression;
import org.omg.sysml.lang.sysml.MetadataAccessExpression;
import org.omg.sysml.lang.sysml.NullExpression;
import org.omg.sysml.lang.sysml.Type;
import org.omg.sysml.expressions.ExpressionEvaluator; 

import gamine.domain.SysMLV2Configuration;
import interfaces.behavior.states.IGuard;

// OUTDATED
public class ExpressionEvaluatorAdapter implements ExpressionEvaluator {
    // Método principal chamado pelo DecisionNodeCommand
    public static boolean evaluate(IGuard guard, SysMLV2Configuration currentState) {
        if (guard == null || guard.getExpression() == null) {
            return true;
        }

        Expression expr = guard.getExpression();

        try {
            Element targetContext = (Element) expr.getOwner(); 
            ExpressionEvaluatorAdapter localEvaluator = new ExpressionEvaluatorAdapter();
            EList<Element> resultList = localEvaluator.evaluate(expr, targetContext);

            if (resultList != null && resultList.size() == 1) {
                Element resultElem = resultList.get(0);
                
                if (resultElem instanceof LiteralBoolean) {
                    return ((LiteralBoolean) resultElem).isValue();
                } else {
                    System.out.println("    [!] A guarda avaliou para um tipo não booleano.");
                    return false;
                }
            } else {
                System.out.println("    [!] A avaliação da guarda retornou uma lista vazia ou nula.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("    [!] Erro interno ao resolver a guarda: " + e.getMessage());
            return false; 
        }
    }

    @Override
    public EList<Element> evaluate(Expression expression, Element target) {
        return null;
    }

    @Override
    public EList<Element> evaluateNull(NullExpression expression, Element target) {
        return null;
    }

    @Override
    public EList<Element> evaluateLiteral(LiteralExpression expression, Element target) {
        return null;
    }

    @Override
    public EList<Element> evaluateFeatureReference(FeatureReferenceExpression expression, Element target) {
        return null;
    }

    @Override
    public EList<Element> evaluateMetadataAccess(MetadataAccessExpression expression, Element target) {
        return null;
    }

    @Override
    public EList<Element> evaluateInvocation(InvocationExpression expression, Element target) {
        return null;
    }

    @Override
    public EList<Element> evaluateConstructor(ConstructorExpression expression, Element target) {
        return null;
    }

    @Override
    public EList<Element> evaluateExpression(Expression expression, Element target, Element... arguments) {
        return null;
    }

    @Override
    public EList<Element> evaluateFeature(Feature feature, Type type) {
        return null;
    }

    @Override
    public EList<Element> evaluateFeatureChain(List<Feature> chainingFeatures, Type type) {
        return null;
    }
}
