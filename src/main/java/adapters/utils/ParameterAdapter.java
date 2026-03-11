package adapters.utils;

import interfaces.utils.IParameter;

import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureDirectionKind;
//import org.omg.sysml.lang.sysml.ReferenceUsage;

public class ParameterAdapter implements IParameter {
	
	private Feature parameter;
	private ActionUsage action;
	
	public ParameterAdapter(Feature parameter) {
		this.parameter = parameter;
	}

	@Override
	public String getName() {
		return parameter.getName();
	}

	@Override
	public String getDeclaredName() {
		return parameter.getDeclaredName();
	}

	@Override
	public String getID() {
		return parameter.getElementId();
	}

	@Override
	public boolean isInput() {
		return parameter.getDirection() == FeatureDirectionKind.IN
				|| parameter.getDirection() == FeatureDirectionKind.INOUT;
	}

	@Override
	public boolean isOutput() {
		return parameter.getDirection() == FeatureDirectionKind.OUT
				|| parameter.getDirection() == FeatureDirectionKind.INOUT;
	}

	@Override
	public FeatureDirectionKind getDirection() {
		return parameter.getDirection();
	}


	@Override
	public void setActionDefinition(ActionUsage actionUsage) {
		this.action = actionUsage;
	}
}