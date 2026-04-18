package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;
import org.omg.sysml.lang.sysml.FlowEnd;
import org.omg.sysml.lang.sysml.FlowUsage;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.ReferenceUsage;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;
import org.omg.sysml.lang.sysml.Usage;

import br.ufrpe.dc.sysml.SysMLV2Spec;

public class DecisionControlTest {
	private static SysMLV2Spec sysmlSpec;
	private static Namespace rootNamespace;

	@BeforeAll
	static void init() {
		sysmlSpec = new SysMLV2Spec();
		sysmlSpec.parseFile("control/FlowUsageExample.sysml");
		rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
		assertNotNull(rootNamespace, "The root namespace must not be null.");
	}
		
	// Recursively prints any element.
	private void printSimpleStructure(Element element, int indent) {
	    String prefix = "  ".repeat(indent);

	    // DeclaredName > getName() > "Unnamed"
	    String declared = element.getDeclaredName() != null
	        ? element.getDeclaredName()
	        : element.getName() != null
	            ? element.getName()
	            : "Unnamed";

	    // Prints class + name
	    System.out.println(prefix + element.eClass().getName() + " - " + declared);

	    // ==== FlowUsage ====
	    if (element instanceof FlowUsage fu) {
	        System.out.printf("%sFlowUsage: %s%n", prefix, declared);

	        // 0) Inspect RelatedFeatures:
	        inspectRelatedFeatures(fu, indent);
	        // 1) Owned features: search for payloads and print info.
	        if (!fu.getOwnedFeature().isEmpty()) {
	            for (Feature of : fu.getOwnedFeature()) {
	                String ofClass = of.eClass().getName();
	                String ofDeclared = of.getDeclaredName() != null ? of.getDeclaredName() : of.getName();
	                // Detects payload by name or heuristic class.
	                boolean isPayload = "payload".equalsIgnoreCase(ofDeclared) || ofClass.toLowerCase().contains("payload");
	                if (isPayload) {
	                    System.out.printf("%s  PayloadFeature - %s%n", prefix, ofDeclared);
	                    // Attempts to print the payload type (if there's a getType()).
	                    try {
	                        var types = of.getType();
	                        if (types != null && !types.isEmpty() && types.get(0) != null) {
	                            String tname = types.get(0).getDeclaredName() != null ? types.get(0).getDeclaredName() : types.get(0).getName();
	                            System.out.printf("%s    payload type: %s%n", prefix, tname);
	                        }
	                    } catch (Exception ex) { 
	                    	
	                    }
	                }
	            }
	        }

	        // 2) Related features (Ed: owners/actions)
	        if (fu.getRelatedFeature() != null && !fu.getRelatedFeature().isEmpty()) {
	            System.out.println(prefix + "  Related Features:");
	            for (Feature rel : fu.getRelatedFeature()) {
	                String relCls = rel.eClass().getName();
	                String relName = rel.getDeclaredName() != null ? rel.getDeclaredName() : rel.getName();
	                // If it's a Usage, prints its name.
	                if (rel instanceof Usage u) {
	                    String uname = u.getDeclaredName() != null ? u.getDeclaredName() : u.getName();
	                    System.out.printf("%s    relatedFeature: %s (%s)%n", prefix, uname, relCls);
	                } else {
	                    // Attempts namingFeature if it's a FeatureReferenceExpression.
	                    if (rel instanceof FeatureReferenceExpression fre) {
	                        try {
	                            String logical = fre.namingFeature().effectiveName();
	                            System.out.printf("%s    relatedFeature: %s (logical: %s)%n", prefix, relName, logical);
	                        } catch (Exception ex) {
	                            System.out.printf("%s    relatedFeature: %s (%s)%n", prefix, relName, relCls);
	                        }
	                    } else {
	                        System.out.printf("%s    relatedFeature: %s (%s)%n", prefix, relName, relCls);
	                    }
	                }
	            }
	        } else {
	            System.out.printf("%s  Related Features: (none)%n", prefix);
	        }

	        // 3) FlowEnds (and owned ReferenceUsages)
	        if (fu.getFlowEnd() != null && !fu.getFlowEnd().isEmpty()) {
	            for (FlowEnd fe : fu.getFlowEnd()) {
	                String feName = fe.getDeclaredName() != null ? fe.getDeclaredName() : "<no-name>";
	                String dir = "<no-dir>";
	                try {
	                    dir = fe.getDirection() != null ? fe.getDirection().getName() : dir;
	                } catch (Exception ex) { /* ignore */ }
	                System.out.printf("%s  FlowEnd - %s [dir=%s]%n", prefix, feName, dir);

	                // ownedFeature inside the FlowEnd (usually ReferenceUsage's).
	                if (!fe.getOwnedFeature().isEmpty()) {
	                    for (Feature ffeat : fe.getOwnedFeature()) {
	                        String fclass = ffeat.eClass().getName();
	                        String fdecl = ffeat.getDeclaredName() != null ? ffeat.getDeclaredName() : (ffeat.getName() != null ? ffeat.getName() : "<no-name>");
	                        System.out.printf("%s    Owned Feature: %s - %s%n", prefix, fclass, fdecl);

	                        // IF ReferenceUsage -> print owningUsage e search ownedRedefinition.
	                        if (ffeat instanceof ReferenceUsage ru) {
	                            // OwningUsage (pode ser null)
	                            Usage owning = ru.getOwningUsage();
	                            String ownName = owning != null ? (owning.getDeclaredName() != null ? owning.getDeclaredName() : owning.getName()) : "null";
	                            System.out.printf("%s      OwningUsage: %s%n", prefix, ownName);

	                            // OwnedRedefinition -> redefined feature -> owner (caso trig/focus)
	                            try {
	                                if (!ru.getOwnedRedefinition().isEmpty() && ru.getOwnedRedefinition().get(0) != null) {
	                                    var red = ru.getOwnedRedefinition().get(0);
	                                    var redefined = red.getRedefinedFeature();
	                                    if (redefined != null && redefined.getOwner() instanceof Usage us) {
	                                        String usn = us.getDeclaredName() != null ? us.getDeclaredName() : us.getName();
	                                        System.out.printf("%s      Redefines feature of: %s%n", prefix, usn);
	                                    } else if (redefined != null) {
	                                        System.out.printf("%s      Redefines feature: %s (owner unknown)%n", prefix, redefined.getDeclaredName() != null ? redefined.getDeclaredName() : redefined.getName());
	                                    }
	                                }
	                            } catch (Exception ex) {
	                                // swallow - structure can vary.
	                            }

	                            // Also try chainingFeature / namingFeature to get a logical name
	                            try {
	                                if (!ru.getChainingFeature().isEmpty()) {
	                                    Feature chain = ru.getChainingFeature().get(0);
	                                    if (chain instanceof FeatureReferenceExpression fr) {
	                                        try {
	                                            String logical = fr.namingFeature().effectiveName();
	                                            System.out.printf("%s      chaining namingFeature: %s%n", prefix, logical);
	                                        } catch (Exception ex) {
	                                            // fallback for declaredName
	                                            System.out.printf("%s      chainingFeature: %s%n", prefix, chain.getDeclaredName());
	                                        }
	                                    } else {
	                                        System.out.printf("%s      chainingFeature: %s%n", prefix, chain.getDeclaredName() != null ? chain.getDeclaredName() : chain.getName());
	                                    }
	                                }
	                            } catch (Exception ex) {

	                            }
	                        } // end ReferenceUsage handling
	                        else {
	                            // Attempts namingFeature for features that are FeatureReferenceExpression.
	                            if (ffeat instanceof FeatureReferenceExpression fre2) {
	                                try {
	                                    String logical = fre2.namingFeature().effectiveName();
	                                    System.out.printf("%s      namingFeature: %s%n", prefix, logical);
	                                } catch (Exception ex) {

	                                }
	                            }
	                        }
	                    } // end for ownedFeature
	                } // end if ownedFeature
	            } // end for flowEnd
	        } // end flowEnd processing

	        // Do not reject here anymore; FlowUsage already analyzed.
	        return;
	    } // end for FlowUsage handling

	    // ==== Generic ReferenceUsage (outside the FlowEnd) ====
	    if (element instanceof ReferenceUsage ru) {
	        Usage owning = ru.getOwningUsage();
	        String ownName = owning != null ? (owning.getDeclaredName() != null ? owning.getDeclaredName() : owning.getName()) : "null";
	        System.out.printf("%s  ReferenceUsage - OwningUsage: %s%n", prefix, ownName);

	        if (!ru.getOwnedRedefinition().isEmpty()) {
	            System.out.println(prefix + "    There is an OwnedRedefinition!");
	            try {
	                var red = ru.getOwnedRedefinition().get(0);
	                var redefined = red.getRedefinedFeature();
	                if (redefined != null && redefined.getOwner() instanceof Usage us) {
	                    String usn = us.getDeclaredName() != null ? us.getDeclaredName() : us.getName();
	                    System.out.printf("%s    Redefines feature of: %s%n", prefix, usn);
	                }
	            } catch (Exception ex) { /* ignore */ }
	        }
	    }

	    // ==== Explores ownedFeatures if the element is a Feature ====
	    if (element instanceof Feature feature) {
	        if (!feature.getOwnedFeature().isEmpty()) {
	            System.out.printf(prefix + "Owned Features:%n");
	        }
	        for (Feature owned : feature.getOwnedFeature()) {
	            printSimpleStructure(owned, indent + 1);
	        }
	    }

	    // ==== Recurse in Namespace (finally) ====
	    if (element instanceof Namespace ns) {
	        for (Element child : ns.getOwnedMember()) {
	            printSimpleStructure(child, indent + 1);
	        }
	    }
	}

	@Test
	void testPrintSimpleModelStructure() {
	    assertNotNull(rootNamespace, "The root namespace must not be null.");
	    System.out.println("=== SIMPLE MODEL STRUCTURE ===");
	    printSimpleStructure(rootNamespace, 0);
	}
	
	// Test method for the relatedFeatures of a FlowUsage.
	private void inspectRelatedFeatures(FlowUsage fu, int indent) {
	    String prefix = "  ".repeat(indent);
	    System.out.printf("%s[inspectRelatedFeatures] FlowUsage: %s%n",
	        prefix, fu.getDeclaredName() != null ? fu.getDeclaredName() : "<no-name>");

	    // 1) Basic info: size
	    System.out.printf("%s  relatedFeature.size = %d%n",
	        prefix, fu.getRelatedFeature() != null ? fu.getRelatedFeature().size() : 0);

	    // 2) Iterate relatedFeature and probe deeply
	    if (fu.getRelatedFeature() != null) {
	        int i = 0;
	        for (Feature rel : fu.getRelatedFeature()) {
	            System.out.printf("%s  related[%d] class=%s%n",
	                prefix, i++, rel != null ? rel.getClass().getSimpleName() : "null");
	            if (rel == null) { continue; }

	            // Try as Usage
	            if (rel instanceof org.omg.sysml.lang.sysml.Usage u) {
	                String name = u.getDeclaredName() != null ? u.getDeclaredName() : u.getName();
	                System.out.printf("%s    as Usage: %s%n", prefix, name);
	                continue;
	            }

	            // Try namingFeature (FeatureReferenceExpression)
	            if (rel instanceof FeatureReferenceExpression fre) {
	                try {
	                    String effective = fre.namingFeature().effectiveName();
	                    System.out.printf("%s    namingFeature.effectiveName(): %s%n", prefix, effective);
	                } catch (Exception ex) {
	                    System.out.printf("%s    namingFeature() not available: %s%n", prefix, ex.getMessage());
	                }
	            }

	            // If ReferenceUsage, try ownedRedefinition -> redefinedFeature -> owner
	            if (rel instanceof ReferenceUsage ru) {
	                System.out.printf("%s    ReferenceUsage declaredName=%s%n", prefix,
	                    ru.getDeclaredName() != null ? ru.getDeclaredName() : "<no-declared>");
	                // owningUsage (may be null)
	                Usage owning = ru.getOwningUsage();
	                System.out.printf("%s      owningUsage = %s%n", prefix,
	                    owning != null ? (owning.getDeclaredName() != null ? owning.getDeclaredName() : owning.getName()) : "null");

	                // ownedRedefinition path
	                if (!ru.getOwnedRedefinition().isEmpty()) {
	                    try {
	                        var red = ru.getOwnedRedefinition().get(0);
	                        var redefined = red.getRedefinedFeature();
	                        System.out.printf("%s      redefinition -> redefinedFeature class=%s name=%s%n", prefix,
	                            redefined != null ? redefined.getClass().getSimpleName() : "null",
	                            redefined != null ? (redefined.getDeclaredName() != null ? redefined.getDeclaredName() : redefined.getName()) : "null");
	                        if (redefined != null && redefined.getOwner() instanceof Usage us) {
	                            String usName = us.getDeclaredName() != null ? us.getDeclaredName() : us.getName();
	                            System.out.printf("%s      redefined owner usage: %s%n", prefix, usName);
	                        }
	                    } catch (Exception ex) {
	                        System.out.printf("%s      error reading ownedRedefinition: %s%n", prefix, ex.getMessage());
	                    }
	                } else {
	                    System.out.printf("%s      no ownedRedefinition%n", prefix);
	                }

	                // chainingFeature fallback
	                if (!ru.getChainingFeature().isEmpty()) {
	                    Feature chain = ru.getChainingFeature().get(0);
	                    System.out.printf("%s      chainingFeature class=%s name=%s%n", prefix,
	                        chain.getClass().getSimpleName(),
	                        chain.getDeclaredName() != null ? chain.getDeclaredName() : chain.getName());
	                    if (chain instanceof FeatureReferenceExpression fr) {
	                        try {
	                            System.out.printf("%s        namingFeature.effectiveName(): %s%n", prefix, fr.namingFeature().effectiveName());
	                        } catch (Exception ex) { /* ignore */ }
	                    }
	                }
	            } // end ReferenceUsage handling
	        } // end for relatedFeature
	    }

	    // 3) Map relatedFeature to FlowEnds (by position) - useful for source/target
	    if (fu.getFlowEnd() != null && fu.getFlowEnd().size() >= 2) {
	        System.out.printf("%s  FlowEnds size=%d, mapping by index (0->source,1->target)%n", prefix, fu.getFlowEnd().size());
	        for (int i = 0; i < Math.min(fu.getRelatedFeature().size(), fu.getFlowEnd().size()); i++) {
	            Feature rel = fu.getRelatedFeature().get(i);
	            FlowEnd fe = fu.getFlowEnd().get(i);
	            String relInfo = rel == null ? "<null>" : (rel.getDeclaredName() != null ? rel.getDeclaredName() : rel.getName());
	            String feName = fe.getDeclaredName() != null ? fe.getDeclaredName() : "<no-name>";
	            System.out.printf("%s    index %d -> related=%s ; flowEnd=%s%n", prefix, i, relInfo, feName);
	        }
	    }
	}


	private void exploreSuccessionFlows(Element elt, int indent) {
		String prefix = "  ".repeat(indent);

		if (elt instanceof SuccessionAsUsage succ) {
			String succName = succ.getDeclaredName() != null ? succ.getDeclaredName() : "<no-name>";
			System.out.printf("%s[SuccessionAsUsage] %s%n", prefix, succName);

			// Traverses direct flows of the succession
			for (Element member : succ.getOwnedMember()) {
				if (member instanceof FlowUsage flow) {
					printFlowDetails(flow, indent + 1);
				}
			}
		}
		// recursion
		if (elt instanceof Namespace ns) {
			for (Element child : ns.getOwnedMember()) {
				exploreSuccessionFlows(child, indent);
			}
		} else if (elt instanceof SuccessionAsUsage nested) {
			for (Element child : nested.getOwnedMember()) {
				exploreSuccessionFlows(child, indent + 1);
			}
		}
	}

	private void printFlowDetails(FlowUsage flow, int indent) {
		
		String prefix = "  ".repeat(indent);
		String flowName = flow.getDeclaredName() != null ? flow.getDeclaredName() : "<no-name>";
		System.out.printf("%s[FlowUsage] %s%n", prefix, flowName);

		// ReferenceUsages and FlowEnd inside ownedFeature
		for (var feat : flow.getOwnedFeature()) {
			if (feat instanceof FlowEnd fe) {
				String feName = fe.getDeclaredName() != null ? fe.getDeclaredName() : "<no-name>";
				System.out.printf("%s  - FlowEnd: %s%n", prefix, feName);

			}
			if (feat instanceof ReferenceUsage ru) {

				String ruName = ru.getDeclaredName() != null ? ru.getDeclaredName() : "<no-name>";
				System.out.printf("%s  - ReferenceUsage: %s%n", prefix, ruName);
				for (Feature features : ru.getChainingFeature()) {
					System.out.println(features.getType().get(0) + features.getDeclaredName());
				}
				if (ru instanceof FeatureReferenceExpression) {

				}
			}
		}

		// FlowEnd - ongoing
		for (FlowEnd end : flow.getFlowEnd()) {
			String directionKind = end.getDirection().getName();
			String endName = end.getDeclaredName() != null ? end.getDeclaredName() : "<no-name>";
			System.out.printf("%s  [FlowEnd] %s kind %s %n", prefix, endName, directionKind);

			if (end.getOwnedReferenceSubsetting() != null) {
				String qName = end.getOwnedReferenceSubsetting().getQualifiedName();
				System.out.printf("%s    - ReferenceSubsetting QN: %s%n", prefix, qName);
			}

			// ReferenceUsages in FlowEnd's ownedFeature.
			for (var feat : end.getOwnedFeature()) {
				if (feat instanceof ReferenceUsage ru2) {
					String ru2Name = ru2.getDeclaredName() != null ? ru2.getDeclaredName() : "<no-name>";
					System.out.printf("%s    - ReferenceUsage: %s%n", prefix, ru2Name);
				}
			}
		}
	}
}
