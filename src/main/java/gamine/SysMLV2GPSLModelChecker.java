package gamine;

import adapters.behavior.actions.ActionUsageAdapter;
import gpsl.modelchecker.StepModelChecker;
import gpsl.syntax.model.State;
import interfaces.behavior.actions.nodes.INode;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.runtime.sli.Step;
import obp3.sli.core.operators.product.Product;
import org.omg.sysml.lang.sysml.Namespace;
import gamine.domain.SysMLV2Configuration;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SysMLV2GPSLModelChecker {

    private final SysMLV2ActionSemantics semantics;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final SysMLV2PropertyAccessor propertyAccessor;

    public record VerificationResult(boolean holds, String trace) {
        @Override
        public String toString() {
            return holds
                ? "✓ Property holds."
                : "✗ Counterexample found:\n" + trace;
        }
    }

    public SysMLV2GPSLModelChecker(ActionUsageAdapter usage, Namespace root) {
        this.semantics = new SysMLV2ActionSemantics(usage);
        this.propertyAccessor = new SysMLV2PropertyAccessor(root);
    }

    public VerificationResult check(String gpslProperty) {
        var checker = new StepModelChecker<>(
                semantics,
                this::evaluateStep,
                gpslProperty
        );
        EmptinessCheckerAnswer<Product<SysMLV2Configuration, State>> answer =
                checker.modelChecker().runAlone();
        
        boolean propertyHolds = answer.holds; 

        StringBuilder sb = new StringBuilder();
        if (!propertyHolds && answer.trace != null) {
            for (Product<SysMLV2Configuration, State> passo : answer.trace) {
                sb.append("  -> ").append(passo.l().toString()).append("\n");
            }
        }
        return new VerificationResult(propertyHolds, sb.toString());
    }

    private boolean evaluateStep(String atom, Step<INode, SysMLV2Configuration> step) {
        atom = atom.trim();
        SysMLV2Configuration config = step.end() != null ? step.end() : step.start();

        if ("done".equals(atom) || "deadlock".equals(atom)) {
            return config.successions.isEmpty() && config.flows.isEmpty();
        }

        try {
            StandardEvaluationContext context = new StandardEvaluationContext(config);
            context.addPropertyAccessor(propertyAccessor);

            Boolean result = parser.parseExpression(atom).getValue(context, Boolean.class);
            return result != null && result;
        } catch (Exception e) {
            return false; 
        }
    }
}