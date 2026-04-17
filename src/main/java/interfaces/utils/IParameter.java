package interfaces.utils;

import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.FeatureDirectionKind;

// Incorporates PartUsage, AttributeUsage and ItemUsage.
public interface IParameter extends INamedElement {
	
	public boolean isInput();
	
	public boolean isOutput();
	
	public FeatureDirectionKind getDirection();

	public void setActionDefinition(ActionUsage actionUsage); // or inherit from another interface?
}
