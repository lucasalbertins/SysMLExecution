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
            INode action,
            SysMLV2Configuration target) {}

    public record VerificationResult(boolean holds, int steps, String witness, String trace) {
        @Override public String toString() {
            if (holds) {
                return "✓ property holds.";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("✗ counterexample at step ").append(steps).append(": ").append(witness);
                if (trace != null && !trace.isEmpty()) {
                    sb.append("\n").append(trace);
                }
                return sb.toString();
            }
        }
    }

    public SysMLV2GPSLModelChecker(ActionUsageAdapter usage, Namespace root) {
        Map<String, Object> initialMemory = collectInitialMemory(root);
        this.semantics = new SysMLV2ActionSemantics(usage, initialMemory);
        this.propertyAccessor = new SysMLV2PropertyAccessor(root);
    }

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
        int realSteps = 0;
        if (answer.trace != null) {
            for (int i = 0; i < statesCount - 1; i++) {
                var src = answer.trace.get(i).l();
                var tgt = answer.trace.get(i + 1).l();
                if (src.successions.isEmpty() && src.flows.isEmpty() && 
                    tgt.successions.isEmpty() && tgt.flows.isEmpty()) {
                    break;
                }
                realSteps++;
            }
        }

        StringBuilder sb = new StringBuilder();
        int violationStep = realSteps;
        String witnessMsg = null;
        if (!answer.holds && answer.trace != null) {
            // Clears the GPSL prefix for display.
            String prop = gpslProperty.replaceFirst("^p\\s*=\\s*!\\s*", "").trim();
            witnessMsg = "'" + prop + "' was violated.";

            // Locates the counterexample step.
            for (int i = 1; i <= realSteps; i++) {
                State autState = answer.trace.get(i).r();
                if (autState != null && autState.name().toLowerCase().contains("accept")) {
                    violationStep = i - 1;
                    break;
                }
            }

            if (statesCount == 1) {
                var config = answer.trace.get(0).l();
                sb.append("  [Initial State / Deadlock]\n");
                sb.append("    ↳ Tokens: [").append(getTokensStr(config)).append("]");
                sb.append(formatMemory(config.memory)).append("\n");
            } else {
                for (int i = 0; i < statesCount - 1; i++) {
                    var sourceConfig = answer.trace.get(i).l();
                    var targetConfig = answer.trace.get(i + 1).l();

                    // step stutter removal
                    if (sourceConfig.successions.isEmpty() && targetConfig.successions.isEmpty() 
                        && sourceConfig.flows.isEmpty() && targetConfig.flows.isEmpty()) {
                        continue;
                    }

                    String actionName = "unknown / internal";
                    for (var srcSucc : sourceConfig.successions) {
                        // If the sequence was in the source but isn't in the target, it was consumed.
                        boolean wasConsumed = targetConfig.successions.stream()
                                .noneMatch(tgtSucc -> tgtSucc.getID().equals(srcSucc.getID()));
                        
                        if (wasConsumed && srcSucc.getTarget() != null) {
                            INode actionNode = srcSucc.getTarget();
                            actionName = actionNode.getDeclaredName() != null 
                                            ? actionNode.getDeclaredName() 
                                            : actionNode.getClass().getSimpleName();
                            break;
                        }
                    }
                    sb.append("  [Step ").append(i).append("]\n");
                    sb.append("    ↳ Source: [").append(getTokensStr(sourceConfig)).append("]");
                    sb.append(formatMemory(sourceConfig.memory)).append("\n");
                    sb.append("    ↳ Action: ").append(actionName).append("\n");
                    sb.append("    ↳ Target: [").append(getTokensStr(targetConfig)).append("]");
                    sb.append(formatMemory(targetConfig.memory)).append("\n");
                    if (i < realSteps - 1) sb.append("\n");
                }
            }
        }

        int finalSteps = answer.holds ? realSteps : violationStep;
        return new VerificationResult(answer.holds, finalSteps, witnessMsg, sb.isEmpty() ? "" : sb.toString());
    }

    private String getTokensStr(SysMLV2Configuration config) {
        return String.join(", ",
                config.successions.stream().map(s -> s.getID().substring(0, 8)).toList());
    }

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
        SysMLV2Configuration startConfig = step.start();
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