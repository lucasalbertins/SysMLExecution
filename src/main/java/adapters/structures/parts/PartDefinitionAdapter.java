package adapters.structures.parts;

import java.util.List;
import java.util.Objects;

import org.omg.sysml.lang.sysml.PartDefinition;

import interfaces.structures.parts.IPartDefinition;

public class PartDefinitionAdapter implements IPartDefinition {
    private final PartDefinition def;

    public PartDefinitionAdapter(PartDefinition def) {
        this.def = Objects.requireNonNull(def, "PartDefinition cannot be null");
    }

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getOwnedFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getOwnedPartUsages() {
		// TODO Auto-generated method stub
		return null;
	}
    
    /*
    @Override
    public String getName() {
        String n = def.getDeclaredName();
        if (n != null && !n.isBlank()) return n;
        n = def.getName();
        return n != null && !n.isBlank() ? n : "<unnamed-partdefinition>";
    }

    @Override
    public List<String> getOwnedFeatures() {
        List<String> out = new ArrayList<>();
        try {
            List<Feature> features = def.getOwnedFeature();
            if (features != null) {
                for (Feature f : features) {
                    if (f == null) continue;
                    String fname = f.getDeclaredName() != null ? f.getDeclaredName() : f.getName();
                    out.add(fname != null ? fname : "<unnamed-feature>");
                }
            }
        } catch (Throwable t) {
            // some implementations may not expose getOwnedFeature reliably; ignore
        }

        try {
            if (def instanceof Namespace ns) {
                for (Element mem : ns.getOwnedMember()) {
                    // se for Feature, já terá sido coberto; caso contrário pegamos declaredName
                    if (!(mem instanceof Feature)) {
                        String mname = mem.getDeclaredName() != null ? mem.getDeclaredName() : mem.getName();
                        if (mname != null && !mname.isBlank()) out.add(mname);
                    }
                }
            }
        } catch (Throwable t) {
            // ignore
        }

        return out.isEmpty() ? Collections.emptyList() : out;
    }

    @Override
    public List<String> getOwnedPartUsages() {
        List<String> out = new ArrayList<>();
        try {
            if (def instanceof Namespace ns) {
                for (Element mem : ns.getOwnedMember()) {
                    if (mem instanceof PartUsage pu) {
                        String puName = pu.getDeclaredName() != null ? pu.getDeclaredName() : pu.getName();
                        out.add(puName != null ? puName : "<unnamed-partusage>");
                    }
                }
            }
        } catch (Throwable t) {
            // ignorar
        }
        return out.isEmpty() ? Collections.emptyList() : out;
    }

    @Override
    public String toString() {
        return String.format("PartDefinition[name=%s, ownedFeatures=%s, ownedPartUsages=%s]",
                getName(), getOwnedFeatures(), getOwnedPartUsages());
    }

    // apenas para testes internos
    protected PartDefinition getUnderlying() { return def; }
    */
}
