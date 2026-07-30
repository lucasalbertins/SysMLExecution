package adapters.sv2pi.utils;

import java.util.HashMap;
import java.util.Map;

import org.omg.sysml.lang.sysml.ActionDefinition;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.util.EvaluationUtil;
import org.omg.sysml.util.FeatureUtil;

public class SysMLV2MemoryExtractor {

    public static Map<String, Object> extract(Namespace root) {
        Map<String, Object> mem = new HashMap<>();
        if (root != null) collectFromElement(root, mem);
        return mem;
    }

    private static void collectFromElement(Element e, Map<String, Object> mem) {
        if (e instanceof Feature f
                && !(e instanceof ActionUsage)
                && !(e instanceof ActionDefinition)
                && f.getDeclaredName() != null) {
            Expression expr = FeatureUtil.getValueExpressionFor(f);
            if (expr != null) {
                Object val = EvaluationUtil.valueOf(expr);
                if (val != null && !(val instanceof Element)) {
                    mem.put(f.getDeclaredName(), val);
                }
            }
        }
        if (e instanceof Namespace ns) {
            for (Element child : ns.getOwnedMember()) {
                collectFromElement(child, mem);
            }
        }
    }
}