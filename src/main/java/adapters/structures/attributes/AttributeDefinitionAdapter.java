package adapters.structures.attributes;

import java.util.List;
import java.util.stream.Collectors;

import org.omg.sysml.lang.sysml.AttributeUsage;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.AttributeDefinition;

import adapters.utils.GenericFeatureAdapter;
import interfaces.structures.attributes.IAttributeDefinition;
import interfaces.utils.IFeature;

public class AttributeDefinitionAdapter implements IAttributeDefinition {
	  private final AttributeDefinition def;
	  
	  public AttributeDefinitionAdapter(AttributeDefinition def) {
	    this.def = def;
	  }

	    @Override
	    public String getName() {
	        return def.getDeclaredName();
	    }

	    @Override
	    public List<IFeature> getOwnedFeatures() {
	        return def.getOwnedMember().stream()
	            .filter(e -> e instanceof Feature)
	            .map(e -> wrapFeature((Feature)e))
	            .collect(Collectors.toList());
	    }

	    // Métodos auxiliares para uso interno
	    private IFeature wrapFeature(Feature f) {
	    	
	        if (f instanceof AttributeUsage) {
	            return (IFeature) new AttributeUsageAdapter((AttributeUsage) f);
	        }
	        if (f instanceof AttributeDefinition) {
	            return new AttributeDefinitionAdapter((AttributeDefinition) f);
	        }
	        // outros adaptadores para outras subclasses de Feature
	        return new GenericFeatureAdapter(f);
	    }

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
