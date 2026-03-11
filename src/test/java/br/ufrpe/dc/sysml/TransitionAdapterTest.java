//package tests;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.omg.sysml.lang.sysml.Namespace;
//import org.omg.sysml.lang.sysml.TransitionUsage;
//
//import adapters.states.TransitionAdapter;
//import interfaces.states.IGuard;
//import interfaces.states.ITrigger;
//import interfaces.states.IEffect;
//import sysml.SysMLV2Spec;
//
//import java.util.List;
//
//class TransitionAdapterTest {
//    static private SysMLV2Spec sysmlSpec;
//    static private List<TransitionUsage> transitions;
//
//    @BeforeAll
//    static void setUp() {
//        // Carrega o arquivo SysML
//        sysmlSpec = new SysMLV2Spec();
//        Namespace rootNamespace = sysmlSpec.getRootNamespace();
//        assertNotNull(rootNamespace, "O namespace raiz não deveria ser nulo");
//
////        // Busca todas as transições dentro do modelo
////        transitions = findAllTransitions(rootNamespace);
////        assertFalse(transitions.isEmpty(), "O modelo não possui nenhuma TransitionUsage.");
//    }
//
//    @Test
//    void testTransitionToString() {
//        for (TransitionUsage transition : transitions) {
//            TransitionAdapter adapter = new TransitionAdapter(transition);
//            String transitionString = adapter.toString();
//            assertNotNull(transitionString, "O toString da transição não pode ser nulo.");
//            System.out.println("Transição encontrada:\n" + transitionString);
//        }
//    }
//
//    @Test
//    void testTransitionHasSourceAndTarget() {
//        for (TransitionUsage transition : transitions) {
//            TransitionAdapter adapter = new TransitionAdapter(transition);
//
//            assertNotNull(adapter.getSource(), "A transição deve ter um estado de origem.");
//            assertNotNull(adapter.getTarget(), "A transição deve ter um estado de destino.");
//
//            System.out.println("Transição de " + adapter.getSource().getName() + " para " + adapter.getTarget().getName());
//        }
//    }
//
//    @Test
//    void testTransitionHasGuard() {
//        for (TransitionUsage transition : transitions) {
//            TransitionAdapter adapter = new TransitionAdapter(transition);
//            IGuard guard = adapter.getGuard();
//
//            if (guard != null) {
//                System.out.println("Guard encontrada: " + guard.toString());
//                assertNotNull(guard, "Se a transição tem guarda, ela não pode ser nula.");
//            } else {
//                System.out.println("Nenhuma Guard encontrada para esta transição.");
//            }
//        }
//    }
//
//    @Test
//    void testTransitionHasTrigger() {
//        for (TransitionUsage transition : transitions) {
//            TransitionAdapter adapter = new TransitionAdapter(transition);
//            ITrigger trigger = adapter.getTrigger();
//
//            if (trigger != null) {
//                System.out.println("Trigger encontrada: " + trigger.toString());
//                assertNotNull(trigger, "Se a transição tem trigger, ele não pode ser nulo.");
//            } else {
//                System.out.println("Nenhum Trigger encontrado para esta transição.");
//            }
//        }
//    }
//
//    @Test
//    void testTransitionHasEffect() {
//        for (TransitionUsage transition : transitions) {
//            TransitionAdapter adapter = new TransitionAdapter(transition);
//            IEffect effect = adapter.getEffect();
//
//            if (effect != null) {
//                System.out.println("Effect encontrado: " + effect.toString());
//                assertNotNull(effect, "Se a transição tem efeito, ele não pode ser nulo.");
//            } else {
//                System.out.println("Nenhum Effect encontrado para esta transição.");
//            }
//        }
//    }
//
////    // Método auxiliar para encontrar todas as transições dentro do namespace
////    private static List<TransitionUsage> findAllTransitions(Namespace namespace) {
////        for (Element e : namespace)
////    }
//}
