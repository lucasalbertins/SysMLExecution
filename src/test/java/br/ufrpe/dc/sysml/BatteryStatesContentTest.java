package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

//import java.util.List;
//import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.AcceptActionUsage;
import org.omg.sysml.lang.sysml.ActionUsage;
//import org.omg.sysml.lang.sysml.AttributeUsage;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.PartDefinition;
import org.omg.sysml.lang.sysml.ReferenceUsage;
import org.omg.sysml.lang.sysml.StateUsage;

// OUTDATED
public class BatteryStatesContentTest {

    static private SysMLV2Spec sysmlSpec;
    static private Namespace rootNamespace;
    static private PartDefinition batteryPart;
    static private StateUsage batteryStates;
    static private StateUsage stateOn;
    static private StateUsage stateOff;

    @BeforeAll
    static void setUp() {
        // Instancia SysMLV2Spec usando o construtor que recebe os caminhos fixos (para testes)
        sysmlSpec = new SysMLV2Spec();
        // Carrega o arquivo BatterySystem.sysml
        sysmlSpec.parseFile("BatterySystem.sysml");

        // Obtém o namespace raiz
        rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "O namespace raiz não deve ser nulo.");

        // Busca a part "Battery" no namespace (busca recursiva)
        batteryPart = findPartDefinitionByName(rootNamespace, "Battery");
        assertNotNull(batteryPart, "A part 'Battery' não foi encontrada no modelo.");

        // Busca recursivamente o StateUsage "BatteryStates"
        batteryStates = findElementByName(rootNamespace, "BatteryStates", StateUsage.class).orElse(null);
        assertNotNull(batteryStates, "O estado 'BatteryStates' não foi encontrado no modelo.");

        // Dentro de BatteryStates, busca os subestados "on" e "off"
        stateOn = findElementByName(batteryStates, "on", StateUsage.class).orElse(null);
        assertNotNull(stateOn, "O estado 'on' não foi encontrado no modelo.");

        stateOff = findElementByName(batteryStates, "off", StateUsage.class).orElse(null);
        assertNotNull(stateOff, "O estado 'off' não foi encontrado no modelo.");
    }

    @Test
    void testPrintStateUsageContent() {
        System.out.println("Conteúdo de BatteryStates:");
        printElementsRecursively(batteryStates, "");
    }

    // Métodos auxiliares para busca recursiva

    private static PartDefinition findPartDefinitionByName(Namespace namespace, String targetName) {
        for (Element element : namespace.getOwnedMember()) {
            if (element instanceof PartDefinition && targetName.equals(element.getDeclaredName())) {
                return (PartDefinition) element;
            }
            if (element instanceof Namespace) {
                PartDefinition found = findPartDefinitionByName((Namespace) element, targetName);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static <T extends Element> Optional<T> findElementByName(Element element, String targetName, Class<T> clazz) {
        if (clazz.isInstance(element) && targetName.equals(element.getDeclaredName())) {
            return Optional.of(clazz.cast(element));
        }
        if (element instanceof Namespace) {
            for (Element child : ((Namespace) element).getOwnedMember()) {
                Optional<T> found = findElementByName(child, targetName, clazz);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }

    @Test
    void testBatteryStatesContainsBatteryInput() {
        // Verifica recursivamente se há um elemento com nome "battery" dentro de BatteryStates
        boolean foundBattery = containsElementWithName(batteryStates, "battery");
        assertTrue(foundBattery, "BatteryStates deve conter um elemento com nome 'battery'.");
    }

    @Test
    void testBatteryInputDirectionInStateOn() {
        // Busca dentro de BatteryStates um elemento chamado "battery" do tipo ReferenceUsage
        Optional<ReferenceUsage> optBatteryReference = findElementByName(batteryStates, "battery", ReferenceUsage.class);
        assertTrue(optBatteryReference.isPresent(), "BatteryStates deve conter uma ReferenceUsage chamada 'battery'.");

        ReferenceUsage batteryReference = optBatteryReference.get();

        // Verifica se a direção é 'in'
        assertNotNull(batteryReference.getDirection(), "A direção da ReferenceUsage 'battery' não pode ser nula.");
        assertEquals("in", batteryReference.getDirection().toString(), "A direção de 'battery' deve ser 'in'.");
        System.out.println("Battery reference direction: " + batteryReference.getDirection());
    }    
    
    @Test
    void testDoActionMonitorBatteryInBatteryStates() {
        // Verifica que a doAction de BatteryStates é 'MonitorBattery'
        ActionUsage doAction = batteryStates.getDoAction();
        assertNotNull(doAction, "A doAction em 'BatteryStates' não deve ser nula.");
        assertEquals("MonitorBattery", doAction.getName(), "A doAction deve ser 'MonitorBattery'.");
        System.out.println("doAction de BatteryStates: " + doAction.getName());
    }
    
    @Test
    void testAcceptActionUsageForTurnOn() {
        // Procura uma AcceptActionUsage cujo declaredName seja "TurnOn" (ajuste conforme o modelo)
        Optional<AcceptActionUsage> optAcceptTurnOn = findAcceptActionUsageByName(rootNamespace, "TurnOn");
        assertTrue(optAcceptTurnOn.isPresent(), "Deve existir uma AcceptActionUsage para 'TurnOn'.");
        AcceptActionUsage acceptTurnOn = optAcceptTurnOn.get();
        
        // Verifica o payload: deve ser uma ReferenceUsage que referencia o AttributeDefinition "TurnOn"
        Optional<String> payloadNameOpt = getPayloadReferencedName(acceptTurnOn);
        assertTrue(payloadNameOpt.isPresent(), "O payload da AcceptActionUsage para TurnOn deve estar definido.");
        String payloadName = payloadNameOpt.get();
        assertEquals("TurnOn", payloadName, "O payload deve referenciar 'TurnOn'.");
        System.out.println("AcceptActionUsage para TurnOn com payload: " + payloadName);
        
        // Verifica o receiver: deve ser um FeatureReferenceExpression
        Expression receiver = acceptTurnOn.getReceiverArgument();
        assertNotNull(receiver, "O receiver da AcceptActionUsage para TurnOn não deve ser nulo.");
        if (receiver instanceof FeatureReferenceExpression) {
            FeatureReferenceExpression refExpr = (FeatureReferenceExpression) receiver;
            Feature referent = refExpr.getReferent();
            assertNotNull(referent, "O referent do receiver não deve ser nulo.");
            System.out.println("AcceptActionUsage para TurnOn com receiver: " + referent.getName());
        } else {
            fail("O receiver da AcceptActionUsage para TurnOn não é do tipo FeatureReferenceExpression.");
        }
    }
    
    @Test
    void testAcceptActionUsageForTurnRecharge() {
        // Procura por uma AcceptActionUsage cujo declaredName seja "TurnRecharge"
        Optional<AcceptActionUsage> optAcceptTurnRecharge = findAcceptActionUsageByName(rootNamespace, "TurnRecharge");
        assertTrue(optAcceptTurnRecharge.isPresent(), "Deve existir uma AcceptActionUsage para 'TurnRecharge'.");
        AcceptActionUsage acceptTurnRecharge = optAcceptTurnRecharge.get();

        // Verifica o payload: deve ser uma ReferenceUsage que referencia um AttributeDefinition com o declaredName "TurnRecharge"
        Optional<String> payloadNameOpt = getPayloadReferencedName(acceptTurnRecharge);
        assertTrue(payloadNameOpt.isPresent(), "O payload da AcceptActionUsage para TurnRecharge deve estar definido.");
        String payloadName = payloadNameOpt.get();
        assertEquals("TurnRecharge", payloadName, "O payload deve referenciar 'TurnRecharge'.");
        System.out.println("AcceptActionUsage para TurnRecharge com payload: " + payloadName);

        // Verifica o receiver (se definido)
        Expression receiver = acceptTurnRecharge.getReceiverArgument();
        if (receiver != null) {
            if (receiver instanceof FeatureReferenceExpression) {
                FeatureReferenceExpression refExpr = (FeatureReferenceExpression) receiver;
                Feature referent = refExpr.getReferent();
                assertNotNull(referent, "O referent do receiver não deve ser nulo.");
                System.out.println("AcceptActionUsage para TurnRecharge com receiver: " + referent.getName());
            } else {
                fail("O receiver da AcceptActionUsage para TurnRecharge não é do tipo FeatureReferenceExpression.");
            }
        } else {
            System.out.println("AcceptActionUsage para TurnRecharge não possui receiver definido (como esperado, se for o caso).");
        }
    }

    
    // Método auxiliar para buscar recursivamente uma AcceptActionUsage por declaredName
    private static Optional<AcceptActionUsage> findAcceptActionUsageByName(Namespace namespace, String targetName) {
        for (Element element : namespace.getOwnedMember()) {
            if (element instanceof AcceptActionUsage && targetName.equals(element.getDeclaredName())) {
                return Optional.of((AcceptActionUsage) element);
            }
            if (element instanceof Namespace) {
                Optional<AcceptActionUsage> found = findAcceptActionUsageByName((Namespace) element, targetName);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }
    
    // Método auxiliar para obter o nome do payload referenciado de uma AcceptActionUsage
    private static Optional<String> getPayloadReferencedName(AcceptActionUsage action) {
        for (Element child : action.getOwnedMember()) {
            String childName = child.getDeclaredName() != null ? child.getDeclaredName() : "UnnamedFeature";
            if ("payload".equals(childName) && child instanceof ReferenceUsage) {
                ReferenceUsage ref = (ReferenceUsage) child;
                if (!ref.getDefinition().isEmpty()) {
                    return Optional.of(ref.getDefinition().get(0).getDeclaredName());
                }
            }
        }
        return Optional.empty();
    }
    
    
    // MÉTODOS AUXILIARES:
    // Método auxiliar para buscar recursivamente um StateUsage por nome
    private static StateUsage findStateUsageByName(Namespace namespace, String targetName) {
        for (Element element : namespace.getOwnedMember()) {
            if (element instanceof StateUsage) {
                StateUsage su = (StateUsage) element;
                if (targetName.equals(su.getDeclaredName())) {
                    return su;
                }
            }
            if (element instanceof Namespace) {
                StateUsage found = findStateUsageByName((Namespace) element, targetName);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    // Método auxiliar para imprimir recursivamente os elementos (classe e declaredName)
    private static void printElementsRecursively(Element element, String indent) {
    	//if (element.getDeclaredName() != null)
    		System.out.println(indent + element.getClass().getSimpleName() + " - " + element.getDeclaredName());
        if (element instanceof Namespace) {
            for (Element child : ((Namespace) element).getOwnedMember()) {
                printElementsRecursively(child, indent + "  ");
            }
        }
    }

    // Método auxiliar para verificar se um elemento com um determinado nome existe na hierarquia
    private static boolean containsElementWithName(Element element, String targetName) {
        if (targetName.equals(element.getDeclaredName())) {
            return true;
        }
        if (element instanceof Namespace) {
            for (Element child : ((Namespace) element).getOwnedMember()) {
                if (containsElementWithName(child, targetName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Método auxiliar para busca recursiva de um elemento pelo nome
    private static <T extends Element> Optional<T> findElementByName(Namespace namespace, String targetName, Class<T> clazz) {
        for (Element element : namespace.getOwnedMember()) {
            if (clazz.isInstance(element) && targetName.equals(element.getDeclaredName())) {
                return Optional.of(clazz.cast(element));
            }
            if (element instanceof Namespace) {
                Optional<T> found = findElementByName((Namespace) element, targetName, clazz);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }
    
}
