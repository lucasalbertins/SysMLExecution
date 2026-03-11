package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.AcceptActionUsage;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.OperatorExpression;
import org.omg.sysml.lang.sysml.StateDefinition;
import org.omg.sysml.lang.sysml.StateUsage;
import org.omg.sysml.lang.sysml.TransitionUsage;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;
import org.omg.sysml.lang.sysml.Expression;

class StateMachineTest {

    static private SysMLV2Spec sysmlSpec;
    static private Namespace rootNamespace;
    static private StateDefinition stateS;

    @BeforeAll
    static void setUp() {
        // Instancia SysMLV2Spec usando o construtor que recebe os caminhos fixos (para testes)
        sysmlSpec = new SysMLV2Spec();
        // Carrega o arquivo 
        sysmlSpec.parseFile("StateTest.sysml");
        
        
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "O namespace raiz não deve ser nulo.");

        // Busca a StateDefinition chamada "S"
        stateS = findStateDefinitionByName(rootNamespace, "S")
                    .orElseThrow(() -> new AssertionError("A definição de estado 'S' não foi encontrada no modelo."));
    }

    @Test
    void testPrintStateDefinitions() {
        System.out.println("Conteúdo de StateTest:");
        printElementsRecursively(rootNamespace, "");
    }

    @Test
    void testStateDefinitionSExists() {
        Optional<StateDefinition> optStateS = findStateDefinitionByName(rootNamespace, "S");
        assertTrue(optStateS.isPresent(), "O estado 'S' deve existir como StateDefinition.");
        stateS = optStateS.get();
    }

    @Test
    void testExitActionExistsInS() {
        Optional<StateUsage> optStateS = findStateUsageByName(rootNamespace, "S");
        assertTrue(optStateS.isPresent(), "O estado 'S' deve existir como StateUsage.");
        StateUsage state = optStateS.get();

        Optional<ActionUsage> optExitAction = Optional.ofNullable(state.getExitAction());
        assertTrue(optExitAction.isPresent(), "O estado 'S' deve conter uma 'exit action'.");
        assertEquals("act", optExitAction.get().getName(), "A 'exit action' de 'S' deve ser 'act'.");
    }
    
    
    @Test
    void testAcceptActionUsageExists() {
        Optional<StateUsage> optStateS1 = findStateUsageByName(rootNamespace, "S1");
        assertTrue(optStateS1.isPresent(), "O estado 'S1' deve existir.");
        StateUsage stateS1 = optStateS1.get();

        boolean hasAcceptAction = stateS1.getOwnedMember().stream()
            .anyMatch(e -> e instanceof AcceptActionUsage && "s".equals(e.getDeclaredName()));

        assertTrue(hasAcceptAction, "O estado 'S1' deve conter uma AcceptActionUsage para 's'.");
    }
    
    
    @Test
    void testTransitionsExist() {
        Optional<StateUsage> optStateS1 = findStateUsageByName(rootNamespace, "S1");
        assertTrue(optStateS1.isPresent(), "O estado 'S1' deve existir.");
        StateUsage stateS1 = optStateS1.get();

        boolean hasTransition = stateS1.getOwnedMember().stream()
            .anyMatch(e -> e instanceof TransitionUsage);

        assertTrue(hasTransition, "O estado 'S1' deve conter uma TransitionUsage.");
    }
    
    
    
    @Test
    void testStateSHasDoAction() {
        Optional<StateUsage> optStateS = findStateUsageByName(rootNamespace, "S");
        assertTrue(optStateS.isPresent(), "O estado 'S' deve existir como StateUsage.");
        StateUsage state = optStateS.get();

        Optional<ActionUsage> optDoAction = Optional.ofNullable(state.getDoAction());
        assertTrue(optDoAction.isPresent(), "O estado 'S' deve conter uma 'do action'.");
        assertEquals("A", optDoAction.get().getName(), "A 'do action' de 'S' deve ser 'A'.");
    }
    
    
    @Test
    void testParallelState() {
        // Verifica se 's' é paralelo
        Optional<StateUsage> optS = findStateUsageByName(rootNamespace, "s");
        assertTrue(optS.isPresent(), "O estado 's' deve existir.");
        StateUsage s = optS.get();

        // isParallel verifica se o estado é paralelo
        assertTrue(s.isParallel(), "O estado 's' deveria ser paralelo, mas não é.");

        // Verifica se s contém s1 e s2, caracterizando paralelismo
        assertStateExistsInParent(s, "s1");
        assertStateExistsInParent(s, "s2");
    }

    @Test
    void testTransitionTDetails() {
        // Busca a transição "T" no modelo
        Optional<TransitionUsage> optTransitionT = findTransitionByName(rootNamespace, "T");
        assertTrue(optTransitionT.isPresent(), "A transição 'T' deve existir.");
        TransitionUsage transitionT = optTransitionT.get();

        // Verifica os estados de origem e destino
        assertNotNull(transitionT.getSource(), "A transição T deve ter um estado de origem.");
        assertNotNull(transitionT.getTarget(), "A transição T deve ter um estado de destino.");
        System.out.println("Source: " + transitionT.getSource().getDeclaredName());
        System.out.println("Target: " + transitionT.getTarget().getDeclaredName());

        // Verifica o TriggerAction usando getTriggerAction() e isTriggerAction()
        AcceptActionUsage triggerAction = (AcceptActionUsage) transitionT.getTriggerAction();
        assertNotNull(triggerAction, "A transição T deve possuir um TriggerAction.");
        assertTrue(triggerAction.isTriggerAction(), "O TriggerAction deve ser identificado como trigger.");
        System.out.println("TriggerAction: " + triggerAction.getDeclaredName());

        // Verifica a guard expression usando getGuardExpression()
        Expression guardExpr = (Expression) transitionT.getGuardExpression();
        assertNotNull(guardExpr, "A transição T deve possuir uma guard expression.");
        if (guardExpr instanceof OperatorExpression) {
            OperatorExpression opExpr = (OperatorExpression) guardExpr;
            System.out.println("Guard Operator: " + opExpr.getOperator());
            // Exemplo: se a guarda for "true", verifique assim:
            assertEquals("true", opExpr.getOperator(), "A guarda deve ser 'true'.");
        } else {
            fail("A guard expression não é do tipo OperatorExpression.");
        }

        // Verifica o effect action usando getEffectAction()
        ActionUsage effectAction = (ActionUsage) transitionT.getEffectAction();
        assertNotNull(effectAction, "A transição T deve possuir uma effect action.");
        System.out.println("Effect Action: " + effectAction.getDeclaredName());
        // verificar se o effect envia um sinal para 'p'?
    }

    // Métodos auxiliares

    private static Optional<TransitionUsage> findTransitionByName(Element element, String targetName) {
        if (element instanceof TransitionUsage && targetName.equalsIgnoreCase(element.getDeclaredName())) {
            return Optional.of((TransitionUsage) element);
        }
        if (element instanceof Namespace) {
            for (Element child : ((Namespace) element).getOwnedMember()) {
                Optional<TransitionUsage> found = findTransitionByName(child, targetName);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }
    
    // Métodos auxiliares

    private static Optional<StateDefinition> findStateDefinitionByName(Element element, String targetName) {
        if (element instanceof StateDefinition && targetName.equalsIgnoreCase(element.getDeclaredName())) {
            return Optional.of((StateDefinition) element);
        }
        if (element instanceof Namespace) {
            for (Element child : ((Namespace) element).getOwnedMember()) {
                Optional<StateDefinition> found = findStateDefinitionByName(child, targetName);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }

    private static Optional<StateUsage> findStateUsageByName(Element element, String targetName) {
        if (element instanceof StateUsage && targetName.equalsIgnoreCase(element.getDeclaredName())) {
            return Optional.of((StateUsage) element);
        }
        if (element instanceof Namespace) {
            for (Element child : ((Namespace) element).getOwnedMember()) {
                Optional<StateUsage> found = findStateUsageByName(child, targetName);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }
    

    private static void printElementsRecursively(Element element, String indent) {
        System.out.println(indent + element.getClass().getSimpleName() + " - " + element.getDeclaredName());
        if (element instanceof Namespace) {
            for (Element child : ((Namespace) element).getOwnedMember()) {
                printElementsRecursively(child, indent + "  ");
            }
        }
    }

    private static void assertStateExistsInParent(StateUsage parent, String childStateName) {
        boolean exists = parent.getOwnedMember().stream()
                               .anyMatch(e -> e instanceof StateUsage && childStateName.equalsIgnoreCase(e.getDeclaredName()));
        assertTrue(exists, "O estado '" + childStateName + "' deveria estar presente dentro de '" + parent.getDeclaredName() + "'.");
    }
}
