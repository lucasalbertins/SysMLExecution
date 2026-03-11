package adapters.structures.expressions;

import java.util.List;
import java.util.stream.Collectors;

import org.omg.sysml.lang.sysml.OperatorExpression;

import interfaces.structures.expressions.IExpression;

public class OperatorExpressionAdapter extends ExpressionAdapter {
    private final OperatorExpression op;

    OperatorExpressionAdapter(OperatorExpression op) {
        super(op);
        this.op = op;
    }

    public String getOperator() {
        return op.getOperator() != null ? op.getOperator() : "<no-operator>";
    }

    public List<IExpression> getOperands() {
        return op.getOperand().stream()
                .map(ExpressionAdapter::of)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        List<IExpression> args = getOperands();
        if (args.size() == 2)
            return args.get(0) + " " + getOperator() + " " + args.get(1);
        else if (args.size() == 1)
            return getOperator() + " " + args.get(0);
        else
            return getOperator() + args.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}
}
