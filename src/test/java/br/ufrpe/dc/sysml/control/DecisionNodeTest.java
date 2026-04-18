package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureChainExpression;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;
import org.omg.sysml.lang.sysml.LiteralBoolean;
import org.omg.sysml.lang.sysml.LiteralInteger;
import org.omg.sysml.lang.sysml.LiteralRational;
import org.omg.sysml.lang.sysml.LiteralString;
import org.omg.sysml.lang.sysml.DecisionNode;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.OperatorExpression;
import org.omg.sysml.lang.sysml.ReferenceUsage;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;
import org.omg.sysml.lang.sysml.Expression; // expressao de guarda

import br.ufrpe.dc.sysml.SysMLV2Spec;

// OUTDATED
class DecisionNodeTest {
	private static SysMLV2Spec sysmlSpec;
	private static Namespace rootNamespace;

	@BeforeAll
	static void init() {
		sysmlSpec = new SysMLV2Spec();
		sysmlSpec.parseFile("control/DecisionExample.sysml");
		rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
		assertNotNull(rootNamespace, "Namespace raiz não deve ser nulo");
	}

	private void printElementStructure(Element element, int indent) {
		String prefix = " ".repeat(indent);
		String className = element.getClass().getSimpleName();
		String name = element.getDeclaredName() != null ? element.getDeclaredName() : "<no-name>";
		System.out.printf("%s%s - %s%n", prefix, className, name);

		// Namespace recursion
		if (element instanceof Namespace ns) {
			for (Element child : ns.getOwnedMember()) {
				printElementStructure(child, indent + 1);
			}
		}

		// DecisionNode exploration
		if (element instanceof DecisionNode dn) {
			if (!dn.getOwnedFeature().isEmpty()) {
				System.out.println(prefix + "Decision Node Features:");
				for (Feature ffeat : dn.getOwnedFeature()) {
					String fclass = ffeat.eClass().getName();
					String fdecl = ffeat.getDeclaredName() != null
							? ffeat.getDeclaredName()
							: (ffeat.getName() != null ? ffeat.getName() : "<no-name>");
					System.out.printf("%s  Owned Feature: %s - %s%n", prefix, fclass, fdecl);
				}
			} else {
				System.out.printf("%sDecision node %s nao possui features%n", prefix, dn.getDeclaredName());
			}
		}

		// Succession connections (source/target)
		if (element instanceof SuccessionAsUsage su) {
			System.out.println(prefix + "SuccessionAsUsage:");
			for (Element src : su.getSource()) {
				String srcName = src.getDeclaredName() != null ? src.getDeclaredName() : "<no-name>";
				System.out.printf("%s  Source -> %s (%s)%n", prefix, srcName, src.getClass().getSimpleName());
			}
			for (Element tgt : su.getTarget()) {
				String tgtName = tgt.getDeclaredName() != null ? tgt.getDeclaredName() : "<no-name>";
				System.out.printf("%s  Target -> %s (%s)%n", prefix, tgtName, tgt.getClass().getSimpleName());
			}
		}

		if (element instanceof Expression expr) {
		    String exprStr = buildExpressionString(expr);

		    if (!exprStr.isBlank()) {
		        System.out.printf("%sExpression (Simplified): %s%n", prefix, exprStr);
		    } else {
		        System.out.printf("%sExpression - %s (%s)%n",
		                prefix,
		                expr.getDeclaredName() != null ? expr.getDeclaredName() : "<no-name>",
		                expr.eClass().getName());
		    }

		    if (!(expr instanceof FeatureChainExpression)
		            && !(expr instanceof FeatureReferenceExpression)
		            && !(expr instanceof OperatorExpression)) {
		        if (!expr.getOwnedFeature().isEmpty()) {
		            System.out.printf("%s  Owned Features:%n", prefix);
		            for (Feature feat : expr.getOwnedFeature()) {
		                String fname = feat.getDeclaredName() != null ? feat.getDeclaredName() : "<no-name>";
		                System.out.printf("%s    Feature: %s (%s)%n", prefix, fname, feat.eClass().getName());
		            }
		        }
		    }

			// Handle FeatureChainExpression
			if (expr instanceof FeatureChainExpression fce) {
				System.out.printf("%s  FeatureChainExpression:%n", prefix);
				if (!fce.getFeature().isEmpty()) {
					for (Feature f : fce.getFeature()) {
						String fname = f.getDeclaredName() != null ? f.getDeclaredName() : "<no-name>";
						System.out.printf("%s    Feature: %s (%s)%n", prefix, fname, f.getClass().getSimpleName());
					}
				}
				if (fce.getTargetFeature() != null) {
					System.out.printf("%s    TargetFeature -> %s%n", prefix, fce.getTargetFeature().getDeclaredName());
				}
				if (fce.sourceTargetFeature()!= null) {
					System.out.printf("%s    SourceTargetFeature -> %s%n", prefix,
							fce.sourceTargetFeature().getDeclaredName());
				}
			}

			// Handle FeatureReferenceExpression
			if (expr instanceof FeatureReferenceExpression fre) {
				System.out.printf("%s  FeatureReferenceExpression:%n", prefix);
				if (fre.getReferent() != null) {
					String referentName = fre.getReferent().getDeclaredName() != null
							? fre.getReferent().getDeclaredName()
							: "<no-referent>";
					System.out.printf("%s    Referent -> %s (%s)%n", prefix, referentName,
							fre.getReferent().getClass().getSimpleName());
				} else {
					System.out.printf("%s    Nenhum Referent%n", prefix);
				}
			}
			
			if (expr instanceof OperatorExpression op) {
			    System.out.println(prefix + "Guard Expression String: " + buildExpressionString(op));
			}


			// Owned features of the Expression
			if (!expr.getOwnedFeature().isEmpty()) {
				System.out.printf("%s  Owned Features:%n", prefix);
				for (Feature feat : expr.getOwnedFeature()) {
					String fname = feat.getDeclaredName() != null ? feat.getDeclaredName() : "<no-name>";
					System.out.printf("%s    Feature: %s (%s)%n", prefix, fname, feat.eClass().getName());
				}
			}
		}
	}
	
	/**
	 * Retorna uma representação textual limpa da expressão,
	 * extraindo apenas as partes relevantes (operandos, operadores e targetFeature).
	 */
	private static String buildExpressionString(Expression expr) {
	    if (expr == null) return "";

	    // Caso 1️⃣: OperatorExpression → combina operandos e operador (ex: monitor.batteryCharge < 100)
	    if (expr instanceof OperatorExpression op) {
	        String operator = op.getOperator() != null ? op.getOperator() : "";

	        List<String> operands = op.getOperand().stream()
	                .map(DecisionNodeTest::buildExpressionString)
	                .filter(s -> !s.isBlank())
	                .toList();

	        if (operands.size() == 2) {
	            return operands.get(0) + " " + operator + " " + operands.get(1);
	        } else if (operands.size() == 1) {
	            return operator + " " + operands.get(0);
	        } else {
	            return String.join(" " + operator + " ", operands);
	        }
	    }

	    // Caso 2️⃣: FeatureChainExpression → pega apenas o nome do targetFeature (ex: batteryCharge)
	    if (expr instanceof FeatureChainExpression chain) {
	        // Operand (geralmente uma FeatureReferenceExpression → "monitor")
	        String left = "";
	        if (!chain.getOperand().isEmpty()) {
	            left = buildExpressionString(chain.getOperand().getFirst());
	        }

	        // TargetFeature (ex: batteryCharge)
	        String targetName = (chain.getTargetFeature() != null
	                && chain.getTargetFeature().namingFeature() != null)
	                ? chain.getTargetFeature().namingFeature().effectiveName()
	                : "";

	        if (!left.isBlank() && !targetName.isBlank()) {
	            return left + "." + targetName;
	        }
	        return targetName.isBlank() ? left : targetName;
	    }

	    // Caso 3️⃣: FeatureReferenceExpression → retorna apenas o nome do referent (ex: monitor)
	    if (expr instanceof FeatureReferenceExpression ref) {
	        if (ref.getReferent() != null && ref.getReferent().namingFeature() != null) {
	            return ref.getReferent().namingFeature().effectiveName();
	        }
	        return "<ref>";
	    }

	    // Caso 4️⃣: Literais (ex: 100, true, "abc")
	    if (expr instanceof LiteralInteger litInt) return String.valueOf(litInt.getValue());
	    if (expr instanceof LiteralBoolean litBool) return String.valueOf(litBool.isValue());
	    if (expr instanceof LiteralRational litReal) return String.valueOf(litReal.getValue());
	    if (expr instanceof LiteralString litStr) return "\"" + litStr.getValue() + "\"";

	    return "";
	}

	@Test
	void testPrintFullModelStructure() {
		assertNotNull(rootNamespace, "Namespace raiz não deve ser nulo");
		System.out.println("=== FULL MODEL STRUCTURE ===");
		printElementStructure(rootNamespace, 0);
	}
}
