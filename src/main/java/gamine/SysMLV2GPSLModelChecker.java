package gamine;

import adapters.behavior.actions.ActionUsageAdapter;
import gpsl.modelchecker.StepModelChecker;
import gpsl.syntax.model.State;
import interfaces.behavior.actions.nodes.INode;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.runtime.sli.Step;
import obp3.sli.core.operators.product.Product;
import org.omg.sysml.lang.sysml.*;
import org.omg.sysml.util.EvaluationUtil;
import org.omg.sysml.util.FeatureUtil;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import gamine.domain.SysMLV2Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SysMLV2GPSLModelChecker {

    private final SysMLV2ActionSemantics semantics;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final SysMLV2PropertyAccessor propertyAccessor;

    public record StepWrapper(
            SysMLV2Configuration source,
            INode                action,
            SysMLV2Configuration target) {}

    public record VerificationResult(boolean holds, int steps, String trace) {
        @Override public String toString() {
            return holds
                ? "✓ Property holds (" + steps + " transitions explored)."
                : "✗ Counterexample found (Length: " + steps + " transitions):\n" + trace;
        }
    }

    public SysMLV2GPSLModelChecker(ActionUsageAdapter usage, Namespace root) {
        Map<String, Object> initialMemory = collectInitialMemory(root);
        this.semantics        = new SysMLV2ActionSemantics(usage, initialMemory);
        this.propertyAccessor = new SysMLV2PropertyAccessor(root);
    }

    // Captures the initial state, filtering out raw AST objects to avoid log clutter.
    private static Map<String, Object> collectInitialMemory(Namespace root) {
        Map<String, Object> mem = new HashMap<>();
        if (root != null) collectFromElement(root, mem);
        return mem;
    }

    private static void collectFromElement(Element e, Map<String, Object> mem) {
        if (e instanceof Feature f
                && !(e instanceof ActionUsage)
                && !(e instanceof ActionDefinition)
                && f.getDeclaredName() != null) {
            Expression expr = FeatureUtil.getValueExpressionFor(f);
            if (expr != null) {
                Object val = EvaluationUtil.valueOf(expr);
                // Only adds if it is a real value (ignores complex EMF/SysML objects)
                if (val != null && !(val instanceof Element)) {
                    mem.put(f.getDeclaredName(), val);
                }
            }
        }
        if (e instanceof Namespace ns) {
            for (Element child : ns.getOwnedMember()) {
                collectFromElement(child, mem);
            }
        }
    }

    public VerificationResult check(String gpslProperty) {
        var checker = new StepModelChecker<>(semantics, this::evaluateStep, gpslProperty);
        EmptinessCheckerAnswer<Product<SysMLV2Configuration, State>> answer =
                checker.modelChecker().runAlone();

        int statesCount = answer.trace != null ? answer.trace.size() : 0;
        int transitions = Math.max(0, statesCount - 1); // Actual number of transitions (Steps)
        StringBuilder sb = new StringBuilder();

        if (!answer.holds && answer.trace != null) {
            if (statesCount == 1) {
                // Extreme case: it failed right at the first state, unable to transition.
                var config = answer.trace.get(0).l();
                sb.append("  [Estado Inicial / Deadlock]\n");
                sb.append("    ↳ Tokens: [").append(getTokensStr(config)).append("]");
                sb.append(formatMemory(config.memory)).append("\n");
            } else {
                // Prints, grouping State (i) as Source and State (i+1) as Target.
                for (int i = 0; i < statesCount - 1; i++) {
                    var sourceConfig = answer.trace.get(i).l();
                    var targetConfig = answer.trace.get(i + 1).l();
                    
                    sb.append("  [Step ").append(i).append("]\n");
                    sb.append("    ↳ Source: [").append(getTokensStr(sourceConfig)).append("]");
                    sb.append(formatMemory(sourceConfig.memory)).append("\n");
                    
                    sb.append("    ↳ Target: [").append(getTokensStr(targetConfig)).append("]");
                    sb.append(formatMemory(targetConfig.memory)).append("\n\n");
                }
            }
        }

        return new VerificationResult(answer.holds, transitions, sb.isEmpty() ? "—" : sb.toString());
    }
    
    private String getTokensStr(SysMLV2Configuration config) {
        return String.join(", ", config.successions.stream().map(s -> s.getID().substring(0, 8)).toList());
    }
    
    // Formats the memory map as text, protected against the printing of complex objects (in case an assignment injects one).
    private String formatMemory(Map<String, Object> memory) {
        if (memory == null || memory.isEmpty()) return "";
        
        String cleanMem = memory.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !(entry.getValue() instanceof Element))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));
                
        return cleanMem.isEmpty() ? "" : " | Memory: {" + cleanMem + "}";
    }

    private boolean evaluateStep(String atom, Step<INode, SysMLV2Configuration> step) {
        atom = atom.trim();
        SysMLV2Configuration startConfig  = step.start();
        SysMLV2Configuration targetConfig = step.end() != null ? step.end() : step.start();
        INode actionNode = step.action().orElse(null);

        if ("done".equals(atom) || "deadlock".equals(atom)) {
            return targetConfig.successions.isEmpty() && targetConfig.flows.isEmpty();
        }

        try {
            StepWrapper root = new StepWrapper(startConfig, actionNode, targetConfig);
            StandardEvaluationContext ctx = new StandardEvaluationContext(root);
            ctx.addPropertyAccessor(propertyAccessor);
            Boolean result = parser.parseExpression(atom).getValue(ctx, Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            System.err.println("[SpEL] Failed to evaluate '" + atom + "': " + e.getMessage());
            return false;
        }
    }
}