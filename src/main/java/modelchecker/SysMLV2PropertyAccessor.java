package modelchecker;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

import semantics.actions.domain.SysMLV2Configuration;

import java.util.Map;
import java.util.HashMap;

public class SysMLV2PropertyAccessor implements PropertyAccessor {

    private final Map<String, Object> globalMemoryFallback;

    public SysMLV2PropertyAccessor(Map<String, Object> initialMemory) {
        this.globalMemoryFallback = initialMemory != null ? initialMemory : new HashMap<>();
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
                case "source": return new TypedValue(wrapper.source());
                case "target": return new TypedValue(wrapper.target());
                case "action": return new TypedValue(wrapper.action());
                case "actionName":
                    var action = wrapper.action();
                    return new TypedValue(action != null ? action.getName() : null);
            }
            return readFromConfiguration(wrapper.target(), name);
        }

        if (target instanceof SysMLV2Configuration config) {
            return readFromConfiguration(config, name);
        }

        throw new AccessException("Property '" + name + "' not found.");
    }

    private TypedValue readFromConfiguration(SysMLV2Configuration config, String name) throws AccessException {
        if ("successions".equals(name)) return new TypedValue(config.successions);
        if ("flows".equals(name))       return new TypedValue(config.flows);
        if ("memory".equals(name))      return new TypedValue(config.memory);

        // 1. Current state in dynamic memory
        if (config.memory != null && config.memory.containsKey(name)) {
            return new TypedValue(config.memory.get(name));
        }

        // 2. Global memory
        if (globalMemoryFallback.containsKey(name)) {
            return new TypedValue(globalMemoryFallback.get(name));
        }

        throw new AccessException("Property '" + name + "' not found in current state or global memory.");
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) {
        return false;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) {
        throw new UnsupportedOperationException(
            "Variable writing should be performed by ActionNodes semantics, not by the SpEL evaluator.");
    }
}