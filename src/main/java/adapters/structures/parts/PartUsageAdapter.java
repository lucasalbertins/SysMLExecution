package adapters.structures.parts;

import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.FeatureDirectionKind;
import org.omg.sysml.lang.sysml.PartUsage;
import org.omg.sysml.lang.sysml.Type;

import java.util.List;
import java.util.Objects;

import interfaces.structures.parts.IPartUsage;

public class PartUsageAdapter implements IPartUsage {
    private final PartUsage usage;

    public PartUsageAdapter(PartUsage usage) {
        this.usage = Objects.requireNonNull(usage, "PartUsage não pode ser nulo");
    }

    @Override
    public String getName() {
        String n = usage.getDeclaredName();
        if (n != null && !n.isBlank()) return n;
        n = usage.getName();
        return n != null && !n.isBlank() ? n : "<unnamed-partusage>";
    }

    @Override
    public String getSpecialization() {
        try {
            List<Type> types = usage.getType();
            if (types != null && !types.isEmpty() && types.get(0) != null) {
                Type t = types.get(0);
                String tn = t.getDeclaredName() != null ? t.getDeclaredName() : t.getName();
                if (tn != null && !tn.isBlank()) return tn;
            }
        } catch (Throwable t) {
            // fallback silencioso
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("PartUsage[name=%s, specialization=%s]", getName(), getSpecialization());
    }

    // expose underlying model only for internal tests (avoid exposing to user)
    protected PartUsage getUnderlying() { return usage; }

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
}
