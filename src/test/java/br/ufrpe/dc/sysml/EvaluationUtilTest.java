package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.*;

import org.omg.sysml.lang.sysml.SysMLFactory;
import org.omg.sysml.lang.sysml.impl.ActionUsageImpl;
import org.omg.sysml.lang.sysml.impl.OperatorExpressionImpl;
import org.omg.sysml.lang.sysml.impl.TransitionUsageImpl;
import org.omg.sysml.util.EvaluationUtil;

import adapters.behavior.actions.ActionDefinitionAdapter;
import adapters.behavior.actions.ActionDefinitionAdapterRegistry;
import adapters.behavior.actions.ActionUsageAdapterRegistry;
import interfaces.behavior.actions.ISuccession;
import interfaces.behavior.actions.nodes.INode;

class EvaluationUtilTest {

    private static SysMLV2Spec spec;
    private static Namespace root;
    private static ActionUsageAdapterRegistry registry;

    @BeforeAll
    static void loadModel() {
        spec = new SysMLV2Spec();
        spec.parseFile("control/DecisionExample.sysml");
        root = (Namespace) spec.getRootNamespace();
        registry = new ActionUsageAdapterRegistry(root);
        assertNotNull(root);
    }


    // pega um atributo por nome dentro do part Vehicle
    
//    private AttributeUsage getAttribute(String name) {
//        ActionDefinition chargeBattery = findActionUsage(root);
//        if (chargeBattery == null) {
//            throw new AssertionError("ActionDefinition 'ChargeBattery' não encontrado no modelo.");
//        }
//
//        for (Element e : chargeBattery.getOwnedMember()) {
//            if (e instanceof AttributeUsage au && name.equals(au.getDeclaredName())) {
//                return au;
//            }
//        }
//
//        throw new AssertionError("Attribute '" + name + "' não encontrado no ChargeBattery.");
//    }

    
//    private PartDefinition findPartDefinition(Namespace root, String name) {
//        if (root instanceof PartDefinition pd && name.equals(pd.getDeclaredName())) {
//            return pd;
//        }
//        for (Element e : root.getOwnedMember()) {
//            if (e instanceof Namespace ns) {
//                PartDefinition found = findPartDefinition(ns, name);
//                if (found != null) return found;
//            }
//        }
//        return null;
//    }
    
    private ActionUsage findActionUsage() {
    	for(Element elt : root.getOwnedElement()) {
    		if (elt instanceof Namespace ns) {
    			System.out.println("AA");
    			for (Element child : ns.getOwnedMember()) {
    				if (child instanceof ActionUsage aui) {
    					if (aui.getDeclaredName().equals("monitor")) {
    						System.out.println("HEY");
    						return aui;
    					}
    				}
    			}
    		}
    	}
    	return null;
//        if (root instanceof ActionDefinition ad && name.equals(ad.getDeclaredName())) {
//            return ad;
//        }
//        for (Element e : root.getOwnedMember()) {
//            if (e instanceof Namespace ns) {
//                ActionDefinition found = findActionDefinition(ns, name);
//                if (found != null) return found;
//            }
//        }
//        return null;
    }



//    private Expression getAttributeExpression(String attrName) {
//
//        AttributeUsage attr = getAttribute(attrName);
//
//        // Caso do Literal direto no OwnedElement
//        for (Element e : attr.getOwnedElement()) {
//            if (e instanceof Expression expr) {
//                return expr;
//            }
//        }
//
//        // Caso do FeatureValue
//        for (Feature f : attr.getOwnedFeature()) {
//            if (f instanceof FeatureValue fv && fv.getOwnedMemberElement() instanceof Expression expr) {
//                return expr;
//            }
//        }
//
//        throw new AssertionError("Nenhuma Expression encontrada para: " + attrName);
//    }


    private Expression unwrap(Expression expr) {
        if (expr instanceof OperatorExpression op &&
            "[]".equals(op.getOperator()) &&
            !op.getArgument().isEmpty()) {

            return op.getArgument().get(0);
        }
        return expr;
    }
    
    private void debugElementStructure(Element e, String indent) {
        System.out.println(indent + e.getClass().getSimpleName() + 
                           " — name=" + e.getDeclaredName());
        Namespace nm = e.getOwningNamespace();
        
        // Owned Elements
        for (Element m : e.getOwnedElement()) {
            System.out.println(indent + "  [Element] " + m.getClass().getSimpleName() +
                               " — name=" + m.getDeclaredName());
            debugElementStructure(m, indent + "    ");
        }

//        // Owned Members
//        for (Element m : nm.getOwnedMember()) {
//            System.out.println(indent + "  [Member] " + m.getClass().getSimpleName() +
//                               " — name=" + m.getDeclaredName());
//            debugElementStructure(m, indent + "    ");
//        }
//        
//        // Owned Relationship
//        for (Element m : nm.getOwnedRelationship()) {
//            System.out.println(indent + "  [Member] " + m.getClass().getSimpleName() +
//                               " — name=" + m.getDeclaredName());
//            debugElementStructure(m, indent + "    ");
//        }
        
    }



//    @Test
//    @DisplayName("valueOf() retorna valores corretos para Literais")
//    void testValueOf() {
//        LiteralInteger li = SysMLFactory.eINSTANCE.createLiteralInteger();
//        li.setValue(42);
//
//        LiteralBoolean lb = SysMLFactory.eINSTANCE.createLiteralBoolean();
//        lb.setValue(true);
//
//        LiteralRational lr = SysMLFactory.eINSTANCE.createLiteralRational();
//        lr.setValue(3.14);
//
//        LiteralString ls = SysMLFactory.eINSTANCE.createLiteralString();
//        ls.setValue("abc");
//
//        assertEquals(42, EvaluationUtil.valueOf(li));
//        assertEquals(true, EvaluationUtil.valueOf(lb));
//        assertEquals(3.14, EvaluationUtil.valueOf(lr));
//        assertEquals("abc", EvaluationUtil.valueOf(ls));
//    }


    // ---------------------------------------------------------
//    @Test
//    @DisplayName("elementFor() converte Java primitives → Literal Expressions")
//    void testElementFor() {
//        Element e1 = EvaluationUtil.elementFor(10);
//        Element e2 = EvaluationUtil.elementFor(true);
//        Element e3 = EvaluationUtil.elementFor("car");
//
//        assertTrue(e1 instanceof LiteralInteger);
//        assertTrue(e2 instanceof LiteralBoolean);
//        assertTrue(e3 instanceof LiteralString);
//
//        assertEquals(10, ((LiteralInteger) e1).getValue());
//        assertEquals(true, ((LiteralBoolean) e2).isValue());
//        assertEquals("car", ((LiteralString) e3).getValue());
//    }


//    @Test
//    @DisplayName("numberOfArgs() em OperatorExpression do maxLoad")
//    void testNumberOfArgs() {
//        Expression expr = getAttributeExpression("maxLoad");
//
//        assertTrue(expr instanceof OperatorExpression);
//
//        OperatorExpression op = (OperatorExpression) expr;
//
//        int args = EvaluationUtil.numberOfArgs(op);
//
//        // operador "[]" deve ter dois argumentos: valor e unidade
//        assertEquals(2, args); 
//    }


//    @Test
//    @DisplayName("evaluate() retorna literal correto (speed = 120.0)")
//    void testEvaluateLiteral() {
//        AttributeUsage speed = getAttribute("speed");
//
//        System.out.println("\n=== DEBUG speed structure ===");
//        debugElementStructure(speed, "");
//
//        Expression expr = getAttributeExpression("speed");
//
//        var result = EvaluationUtil.evaluate(expr, speed);
//        assertEquals(1, result.size());
//        expr = unwrap(expr);
//
//        //EList<Element> result = EvaluationUtil.evaluate(expr, root);
//
//        assertEquals(1, result.size());
//        Element r = result.get(0);
//
//        assertTrue(r instanceof LiteralRational);
//        assertEquals(120.0, ((LiteralRational) r).getValue());
//    }


//    @Test
//    @DisplayName("expressionFor() reconstrói expressão a partir de valores")
//    void testExpressionFor() {
//        // cria lista de elementos
//        EList<Element> list = EvaluationUtil.integerResult(7);
//
//        Expression expr = EvaluationUtil.expressionFor(list, root);
//        System.out.println("aaa " + expr.checkCondition(expr));
//
//        assertNotNull(expr);
//        assertTrue(expr instanceof LiteralInteger);
//        assertEquals(7, ((LiteralInteger) expr).getValue());
//    }
    
    public void printElementStructure(Element element, int indent) {
    	String prefix = " ".repeat(indent);
    	String className = element.getClass().getSimpleName();
    	String name = element.getDeclaredName() != null ? element.getDeclaredName() : "<no-name>";
    	System.out.printf("%s%s - %s%n", prefix, className, name);
    	
    	if (element instanceof Namespace ns) {
    		for (Element child : ns.getOwnedMember()) {
    			if (child instanceof OperatorExpressionImpl oei)  {
    				System.out.println(oei);
    				Element elet = registry.getByDeclaredName("monitor").getFirst().getElement();
    				System.out.println(oei.checkCondition(ns));
    			}
    			printElementStructure(child, indent + 1);
    		}
    	}
    }
    
    
    @Test
    void testDecision() {
    	printElementStructure(root.getOwnedElement().getFirst(), 3);

    	
//    	ActionDefinitionAdapter adapter = registry.getByDeclaredName("ChargeBattery").getFirst();
//    	for (INode node : adapter.getNodes()) {
//    		if (node.getDeclaredName().equals("decision1")) {
//    			for (Element elt : node.getElement()) {
//    				System.out.println("a");
//    				if (elt instanceof TransitionUsageImpl tu) {
//    					System.out.println("AABB");
//    					for (Element elemento : tu.getOwnedElement()) {
//    						if (elemento instanceof OperatorExpression oe) {
//    							System.out.println(oe.checkCondition(oe));
//    						}
//    					}
//    				}
//    			}
//    		}
//    	}
    	
    	//Expression expr = getAttributeExpression("batteryCharge");
    	//System.out.println("aa " + expr.checkCondition(expr));
    }
}