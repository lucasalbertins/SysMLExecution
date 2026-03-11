package br.ufrpe.dc.sysml;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.*;


class ActionFlowSuccessionsTest {

    private static SysMLV2Spec spec;
    private static Namespace root;
    private static ActionUsage action0;

    @BeforeAll
    static void init() {
        spec = new SysMLV2Spec();
        spec.parseFile("ActionFlow.sysml");

        root = spec.getRootNamespace();
        assertNotNull(root, "RootNamespace não pode ser nulo");

        action0 = findAction("action0");
        assertNotNull(action0, "Não encontrou a ActionUsage 'action0'");
    }

    @Test
    void testOrderStartToDone() {
        // deque de pares fonte->alvo esperados
        String[][] expected = {
            {"start",     "action1"},
            {"action1",   "action2"},
            {"action2",   "action3"},
            {"action3",   "action4"},
            {"action4",   "done"}
        };

        for (String[] pair : expected) {
            String source = pair[0], target = pair[1];
            String actual = findSuccessorOf(source);
            assertEquals(target, actual,
              "Erro na succession: de '"+ source +"' esperava '"+ target +"' mas foi '"+ actual +"'");
        }
    }

    // Procura recursivamente a partir do root uma ActionUsage com o nome
    private static ActionUsage findAction(String name) {
        return findActionIn(root, name);
    }
    private static ActionUsage findActionIn(Element e, String name) {
        if (e instanceof ActionUsage a && name.equals(a.getDeclaredName())) {
            return a;
        }
        if (e instanceof Namespace ns) {
            for (Element child: ns.getOwnedMember()) {
                ActionUsage found = findActionIn(child, name);
                if (found != null) return found;
            }
        }
        return null;
    }
//System.out.println("Membership: " + membership.getClass().getSimpleName());
    // Procura a succession a partir do nome da source action
    private static String findSuccessorOf(String sourceName) {
        for (Element membership : action0.getOwnedMember()) {
            if (!(membership instanceof SuccessionAsUsage)) continue;
            SuccessionAsUsage su = (SuccessionAsUsage) membership;

            EList<Element> targets = su.getTarget();
            String tgtName = null;
            if (!targets.isEmpty() && targets.get(0) instanceof ActionUsage) {
                tgtName = ((ActionUsage) targets.get(0)).getDeclaredName();
            }

            EList<Element> sources = su.getSource();
            // percorre a lista até encontrar um source correspondente
            for (Element srcElem : sources) {
                String srcName = (srcElem instanceof ActionUsage)
                    ? ((ActionUsage) srcElem).getDeclaredName()
                    : null;

                if (sourceName.equals(srcName)) {
                    System.out.printf("DEBUG Succession candidate: %s → %s%n", srcName, tgtName);
                    return tgtName;  
                }
            }
        }
        return null; 
    }




//    private static String findSuccessorOf(String sourceName) {
//        for (Element membership : action0.getOwnedMember()) {
//            System.out.println("Membership: " + membership.getClass().getSimpleName());
//
//            SuccessionAsUsage sg;
//            
//        	if (membership instanceof SuccessionAsUsage su) {
//        		System.out.println("Membership: " + membership.getClass().getSimpleName());
//        		String suName = su.getDeclaredName();
//        		System.out.println("Succession: " + suName);
//        		
//                ActionUsage src = (ActionUsage) su.getSource();
//                if (src != null && sourceName.equals(src.getDeclaredName())) {
//                    ActionUsage tgt = (ActionUsage) su.getTarget();
//                    
//                    return tgt != null ? tgt.getDeclaredName() : null;
//                }
//            }
//        }
//        return null;
//    }

}
