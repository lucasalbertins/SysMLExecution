package br.ufrpe.dc.sysml;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.StateUsage;

// OUTDATED
public class SysMLV2SpecTest {
	static private SysMLV2Spec sysmlSpec;

    @BeforeAll
    static void setUp() {
        sysmlSpec = new SysMLV2Spec();
    }

    @Test 
    void testRootNamespaceNotNull() {
        Namespace rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "O namespace raiz não deveria ser nulo");
    }

    @Test //Verificar se há elementos dentro do Namespace raiz
    void testRootNamespaceHasElements() {
        Namespace rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "O namespace raiz não deveria ser nulo");
        assertFalse(rootNamespace.getOwnedMember().isEmpty(), "O namespace raiz deve conter elementos");
    }
    
    @Test //Verificar se há elementos StateUsage no arquivo
    void testStateUsageDetection() {
        Namespace rootNamespace = (Namespace) sysmlSpec.getRootNamespace();
        assertNotNull(rootNamespace, "O namespace raiz não deveria ser nulo");

        // Lista recursivamente todos os elementos no namespace
        boolean hasStateUsage = containsStateUsage(rootNamespace);

        assertTrue(hasStateUsage, "O arquivo deve conter pelo menos um StateUsage");
    }

    // Método auxiliar para busca recursiva dos elementos do namespace
    private boolean containsStateUsage(Namespace namespace) {
        for (Element element : namespace.getOwnedMember()) {
            System.out.println(element.getClass().getSimpleName() + " - " + element.getDeclaredName());

            if (element instanceof StateUsage) {
                return true; // StateUsage encontrado
            }

            // Se o elemento for outro Namespace, procurar dentro dele
            if (element instanceof Namespace) {
                if (containsStateUsage((Namespace) element)) {
                    return true;
                }
            }
        }
        return false;
    }


}
