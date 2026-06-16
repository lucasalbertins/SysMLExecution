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

/**
 * Injetor customizado do Spring SpEL.
 * Ensina o motor de expressões a ler variáveis do Snapshot de Memória ou da AST do SysML v2.
 */
public class SysMLV2PropertyAccessor implements PropertyAccessor {
    
    private final Namespace rootNamespace;
    private final Map<String, Feature> featureCache = new HashMap<>();

    public SysMLV2PropertyAccessor(Namespace rootNamespace) {
        this.rootNamespace = rootNamespace;
    }

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{SysMLV2Configuration.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return true; 
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
        SysMLV2Configuration config = (SysMLV2Configuration) target;

        // 1. Tenta ler o estado mutável do Snapshot (Memória de simulação atual)
        if (config.memory != null && config.memory.containsKey(name)) {
            return new TypedValue(config.memory.get(name));
        }

        // 2. BUSCA AUTOMÁTICA NO MODELO: Se a variável ainda não foi modificada, 
        // busca o seu valor padrão/inicial direto na árvore (AST) do SysML v2.
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

        // 3. Variável inexistente no modelo
        throw new AccessException("SysML Feature '" + name + "' not found.");
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) {
        return false;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) {
        throw new UnsupportedOperationException("A escrita de variáveis deve ser feita pelos ActionNodes, não pelo avaliador lógico.");
    }

    // Busca recursiva dentro da árvore do modelo do usuário
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