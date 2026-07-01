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

    // A wrapper to facilitate the complete transition to Spring
    public record StepWrapper(SysMLV2Configuration source, INode action, SysMLV2Configuration target) {}

    public record VerificationResult(boolean holds, int steps, String trace) {
        @Override
        public String toString() {
            return holds
                ? "✓ Property holds (" + steps + " steps explored)."
                : "✗ Counterexample found (Length: " + steps + " steps):\n" + trace;
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

        int steps = answer.trace != null ? answer.trace.size() : 0;
        StringBuilder sb = new StringBuilder();
        
        if (!answer.holds && answer.trace != null) {
            int stepIndex = 0;
            for (var passo : answer.trace) {
                var config = passo.l(); // SysMLV2Configuration
                sb.append("  [Step ").append(stepIndex++).append("] ");
                if (!config.memory.isEmpty()) {
                    sb.append(" Memory: ").append(config.memory);
                }
                sb.append("\n");
            }
        }
        return new VerificationResult(answer.holds, steps, sb.isEmpty() ? "—" : sb.toString());
    }

    private boolean evaluateStep(String atom, Step<INode, SysMLV2Configuration> step) {
        
        atom = atom.trim();
        SysMLV2Configuration startConfig = step.start();
        SysMLV2Configuration targetConfig = step.end() != null ? step.end() : step.start();
        INode actionNode = step.action().orElse(null);

        // Topological validation
        if ("done".equals(atom) || "deadlock".equals(atom)) {
            return targetConfig.successions.isEmpty() && targetConfig.flows.isEmpty();
        }

        try {
            // We encapsulate the transition in an object containing source, target, and action.
            StepWrapper rootObject = new StepWrapper(startConfig, actionNode, targetConfig);
            
            // The context now originates from the Wrapper, not just the final target.
            StandardEvaluationContext context = new StandardEvaluationContext(rootObject);
            context.addPropertyAccessor(propertyAccessor);

            Boolean result = parser.parseExpression(atom).getValue(context, Boolean.class);
            return result != null && result;
        } catch (Exception e) {
            System.err.println("[SpEL ERROR] Failed to evaluate '" + atom + "': " + e.getMessage());
            return false; 
        }
    }
}