package adapters.structures.expressions;

import org.omg.sysml.lang.sysml.*;

class FeatureReferenceExpressionAdapter extends ExpressionAdapter {
    private final FeatureReferenceExpression ref;

    FeatureReferenceExpressionAdapter(FeatureReferenceExpression ref) {
        super(ref);
        this.ref = ref;
    }

    public String getReferentName() {
        if (ref.getReferent() != null)
            return ref.getReferent().getDeclaredName() != null ? ref.getReferent().getDeclaredName()
                    : ref.getReferent().getName();
        return "<unresolved-ref>";
    }

    @Override
    public String toString() {
        return getReferentName();
    }

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}
}

