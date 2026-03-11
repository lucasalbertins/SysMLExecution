package adapters.utils;

import org.omg.sysml.lang.sysml.Feature;

import interfaces.utils.IFeature;

public class GenericFeatureAdapter implements IFeature {
	  private final Feature f;
	  public GenericFeatureAdapter(Feature f) { this.f = f; }
	  @Override 
	  public String getName() { return f.getDeclaredName(); }
	  
	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isOrdered() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isUnique() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isComposite() {
		// TODO Auto-generated method stub
		return false;
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
	}
