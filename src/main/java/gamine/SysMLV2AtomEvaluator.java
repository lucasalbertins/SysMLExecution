package gamine;

import org.omg.sysml.lang.sysml.*;
import org.omg.sysml.util.EvaluationUtil;
import org.omg.sysml.util.FeatureUtil;
import gamine.domain.SysMLV2Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SysMLV2AtomEvaluator {

    private Namespace rootNamespace;
    private Map<String, Feature> featureCache = new HashMap<>();

    // padrões de string para testar propriedades
    private Pattern NUMERIC = Pattern.compile(
        "([a-zA-Z][a-zA-Z_0-9]*)\\s*(>=|<=|==|!=|>|<)\\s*([\\d.]+)");
    private Pattern BOOLEAN = Pattern.compile(
        "([a-zA-Z][a-zA-Z_0-9]*)\\s*(==|!=)\\s*(true|false)");

    public SysMLV2AtomEvaluator(Namespace rootNamespace) {
        this.rootNamespace = rootNamespace;
    }
    
    // recebe a feature através do mapeamento, pega sua expression e retorna seu valor
    private Object readValue(String name) {
        Feature f = featureCache.computeIfAbsent(name, n -> search(rootNamespace, n));
        if (f == null) throw new IllegalStateException("Feature not found: " + name);
        Expression e = FeatureUtil.getValueExpressionFor(f);
        if (e == null) throw new IllegalStateException("'" + name + "' has no FeatureValue");
        return EvaluationUtil.valueOf(e);
    }
    
    // busca recursiva para encontrar a variável especificada no modelo
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

    // analisar cada tipo de operação
    private boolean compareNum(double a, String op, double b) {
        return switch (op) {
            case ">=" -> a >= b;
            case "<=" -> a <= b;
            case "==" -> a == b;
            case "!=" -> a != b;
            case ">" -> a > b;
            case "<" -> a < b;
            default -> throw new UnsupportedOperationException(op);
        };
    }

    // avaliar a string de acordo com o padrão adotado
    public boolean evaluate(String atom, SysMLV2Configuration config) {
        atom = atom.trim();

        if ("done".equals(atom) || "deadlock".equals(atom)) {
            return config.successions.isEmpty() && config.flows.isEmpty();
        }

        // group(1) -> variável da esquerda, group(2) -> operação, group(3) -> valor num/bool
        var numM = NUMERIC.matcher(atom);
        if (numM.matches()) {
            Object lhs = readValue(numM.group(1));
            double rhs = Double.parseDouble(numM.group(3));
            if (lhs instanceof Number n) return compareNum(n.doubleValue(), numM.group(2), rhs);
            throw new IllegalStateException("'" + numM.group(1) + "' is not numeric");
        }

        var boolM = BOOLEAN.matcher(atom);
        if (boolM.matches()) {
            Object lhs = readValue(boolM.group(1));
            boolean rhs = Boolean.parseBoolean(boolM.group(3));
            if (lhs instanceof Boolean b) return "==".equals(boolM.group(2)) ? b == rhs : b != rhs;
            throw new IllegalStateException("'" + boolM.group(1) + "' is not boolean");
        }
        throw new IllegalArgumentException("Cannot evaluate atom: '" + atom + "'");
    }
}
