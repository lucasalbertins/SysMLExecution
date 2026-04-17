package adapters.structures.attributes;

import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.AttributeUsage;
import org.omg.sysml.lang.sysml.FeatureDirectionKind;
import interfaces.structures.attributes.IAttributeUsage;

public class AttributeUsageAdapter implements IAttributeUsage {
    private final AttributeUsage usage;

    public AttributeUsageAdapter(AttributeUsage usage) {
        this.usage = usage;
    }

    @Override
    public String getName() {
        return usage.getDeclaredName();
    }

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInput() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOutput() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FeatureDirectionKind getDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeclaredName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActionDefinition(ActionUsage actionUsage) {
		// TODO Auto-generated method stub
		
	}

//    @Override
//    public String getType() {
//        return usage.getType() != null ? ((Element) usage.getType()).getName() : "Unknown";
//    }
//
//    @Override
//    public String getValue() {
//        ExpressionAdapter exprAdapter = getFeatureValueExpression();
//        return exprAdapter != null ? exprAdapter.getValue() : "None";
//    }
//
//    @Override
//    public String getUnit() {
//        ExpressionAdapter exprAdapter = getFeatureValueExpression();
//        return exprAdapter != null ? exprAdapter.getUnit() : "None";
//    }
//
//    private ExpressionAdapter getFeatureValueExpression() {
//        for (Feature feature : usage.getOwnedFeature()) {
//            if (feature instanceof FeatureValue fv && fv.getOwnedMemberElement() instanceof OperatorExpression op) {
//                return new ExpressionAdapter(op);
//            }
//        }
//        return null;
//    }
}
