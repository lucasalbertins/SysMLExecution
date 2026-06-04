package gamine;

import adapters.behavior.actions.ActionUsageAdapter;
import gamine.domain.SysMLV2Configuration;
import obp3.Sequencer;
import obp3.sli.core.operators.ToDetermistic;
import org.omg.sysml.lang.sysml.Namespace;

import java.util.function.Predicate;

public class SysMLV2ModelChecker {

    private final SysMLV2ActionSemantics semantics;
    private final SysMLV2AtomEvaluator atomEval;
    private final long seed;
    private static final int MAX_STEPS = 500; 

    public record VerificationResult(
            boolean holds, // indica se foi comprovado ou não
            int steps, // indica o número de etapas que levou para provar/descomprovar
            String witness) { // mostra quem descomprova (contraexemplo)

        @Override
        public String toString() {
            return holds
                ? "✓ holds (" + steps + " steps explored)"
                : "✗ counterexample at step " + steps + ": " + witness;
        }
    }

    // utiliza seed para randomPolicy, poderia ser abstraído
    public SysMLV2ModelChecker(ActionUsageAdapter usage, Namespace root) {
        this(usage, root, 42L);
    }

    public SysMLV2ModelChecker(ActionUsageAdapter usage, Namespace root, long seed) {
        this.semantics = new SysMLV2ActionSemantics(usage);
        this.atomEval = new SysMLV2AtomEvaluator(root);
        this.seed = seed;
    }

    // propriedades
    public VerificationResult checkNever(String atom) {
        int[] steps = {0};
        boolean[] violated = {false};

        run(config -> {
            steps[0]++;
            if (atomEval.evaluate(atom, config)) {
                violated[0] = true;
                return true; 
            }
            return steps[0] > MAX_STEPS;
        });

        return violated[0]
            ? new VerificationResult(false, steps[0], "'" + atom + "' became true")
            : new VerificationResult(true, steps[0], null);
    }

    public VerificationResult checkEventually(String atom) {
        int[] steps = {0};
        boolean[] reached = {false};

        run(config -> {
            steps[0]++;
            if (atomEval.evaluate(atom, config)) {
                reached[0] = true;
                return true; 
            }
            return steps[0] > MAX_STEPS;
        });

        return reached[0]
            ? new VerificationResult(true, steps[0], null)
            : new VerificationResult(false, steps[0], "'" + atom + "' was never true in " + steps[0] + " steps");
    }
    
    public VerificationResult checkAlways(String atom) {
        int[] steps = {0};
        boolean[] violated = {false};

        run(config -> {
            steps[0]++;
            if (!atomEval.evaluate(atom, config)) {
                violated[0] = true;
                return true; 
            }
            return steps[0] > MAX_STEPS;
        });

        return violated[0]
            ? new VerificationResult(false, steps[0], "'" + atom + "' became false")
            : new VerificationResult(true, steps[0], null);
    }

    private void run(Predicate<SysMLV2Configuration> stopIf) {
        var det = ToDetermistic.randomPolicy(semantics, seed);
        var sequencer = new Sequencer<>(det);
        sequencer.run(stopIf::test);
    }
}
