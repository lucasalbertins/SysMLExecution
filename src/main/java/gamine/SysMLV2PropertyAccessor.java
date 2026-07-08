package gamine;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.util.EvaluationUtil;
import org.omg.sysml.util.FeatureUtil;

import gamine.domain.SysMLV2Configuration;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

import java.util.HashMap;
import java.util.Map;

public class SysMLV2PropertyAccessor implements PropertyAccessor {

    private final Namespace rootNamespace;
    private final Map<String, Feature> featureCache = new HashMap<>();

    public SysMLV2PropertyAccessor(Namespace rootNamespace) {
        this.rootNamespace = rootNamespace;
    }

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{
            SysMLV2Configuration.class,
            SysMLV2GPSLModelChecker.StepWrapper.class
        };
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return true;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {

        if (target instanceof SysMLV2GPSLModelChecker.StepWrapper wrapper) {
            switch (name) {
                case "source":     return new TypedValue(wrapper.source());
                case "target":     return new TypedValue(wrapper.target());
                case "action":     return new TypedValue(wrapper.action());
                case "actionName":
                    var action = wrapper.action();
                    return new TypedValue(action != null ? action.getDeclaredName() : null);
            }
            return readFromConfiguration(wrapper.target(), name);
        }

        if (target instanceof SysMLV2Configuration config) {
            return readFromConfiguration(config, name);
        }

        throw new AccessException("SysML Feature '" + name + "' not found.");
    }


    private TypedValue readFromConfiguration(SysMLV2Configuration config, String name) throws AccessException {

        if ("successions".equals(name)) return new TypedValue(config.successions);
        if ("flows".equals(name))       return new TypedValue(config.flows);
        if ("memory".equals(name))      return new TypedValue(config.memory);

        if (config.memory != null && config.memory.containsKey(name)) {
            return new TypedValue(config.memory.get(name));
        }

        if (rootNamespace != null) {
            Feature f = featureCache.computeIfAbsent(name, n -> search(rootNamespace, n));
            if (f != null) {
                Expression e = FeatureUtil.getValueExpressionFor(f);
                if (e != null) {
                    Object val = EvaluationUtil.valueOf(e);
                    return new TypedValue(val);
                }
            }
        }

        throw new AccessException("SysML Feature '" + name + "' not found.");
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) {
        return false;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) {
        throw new UnsupportedOperationException(
            "Variable writing should be performed by ActionNodes, not by the logic evaluator.");
    }

    private Feature search(Element e, String name) {
        if (e instanceof Feature f && name.equals(f.getDeclaredName())) return f;
        if (e instanceof Namespace ns) {
            for (Element child : ns.getOwnedMember()) {
                Feature found = search(child, name);
                if (found != null) return found;
            }
        }
        return null;
    }
}
