package adapters.structures.expressions;

import java.util.ArrayList;
import java.util.List;

import org.omg.sysml.lang.sysml.FeatureChainExpression;

// OUTDATED
public class FeatureChainExpressionAdapter extends ExpressionAdapter {
    private final FeatureChainExpression chain;

    FeatureChainExpressionAdapter(FeatureChainExpression chain) {
        super(chain);
        this.chain = chain;
    }

    public List<String> getFeatureChain() {
        List<String> features = new ArrayList<>();

        // Obtém o operador de encadeamento (geralmente ".")
        String operator = chain.getOperator() != null ? chain.getOperator() : ".";

        // Primeiro, tenta extrair o FeatureReferenceExpression
        chain.getFeature().forEach(f -> {
            if (f.getName() != null && !f.getName().isBlank()) {
                features.add(f.getName());
            }
        });

        // Caso tenha TargetFeature (importante em guards e flows)
        if (chain.getTargetFeature() != null) {
            String targetName = chain.getTargetFeature().getDeclaredName();
            if (targetName == null) targetName = chain.getTargetFeature().getName();
            if (targetName != null) features.add(targetName);
        }

        return features;
    }

    @Override
    public String toString() {
        return String.join(".", getFeatureChain());
    }

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}
}
