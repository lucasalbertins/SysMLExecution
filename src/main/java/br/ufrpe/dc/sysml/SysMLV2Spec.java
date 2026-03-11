package br.ufrpe.dc.sysml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.omg.kerml.xtext.KerMLStandaloneSetup;
import org.omg.sysml.interactive.SysMLInteractive;
import org.omg.sysml.interactive.SysMLInteractiveResult;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.SysMLPackage;
import org.omg.sysml.util.ElementUtil;
import org.omg.sysml.xtext.SysMLStandaloneSetup;

public class SysMLV2Spec {

    private SysMLInteractive sysml;
    private Namespace rootNamespace;
    private String baseFilePath;

    public SysMLV2Spec(){
    	Properties properties = new Properties();
    	try {
			properties.load(new FileReader("src/main/resources/application.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	baseFilePath = properties.getProperty("app.baseFilePath");
        String systemLibPath = properties.getProperty("app.systemLibPath");
        if (systemLibPath == null || systemLibPath.isEmpty()) {
            throw new RuntimeException("Erro: A variável de ambiente SYSTEM_LIB_PATH não foi definida.");
        }

        sysml = SysMLInteractive.getInstance();
        sysml.setVerbose(false);
        sysml.loadLibrary(systemLibPath);
        sysml.setApiBasePath("http://sysml2.intercax.com:9000");
    }
    
    public void parseFileWithTransform(String fileName) {
        // Monta o caminho completo
        String fullPath = baseFilePath + "/" + fileName;

        KerMLStandaloneSetup.doSetup();
        SysMLStandaloneSetup.doSetup();
        
        SysMLPackage.eINSTANCE.eClass();

        // cria um ResourceSet e carrega o resource
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.getResource(URI.createFileURI(fullPath), true);

        EcoreUtil.resolveAll(resourceSet);
        ElementUtil.transformAll(resourceSet, true);

        // extrai o Namespace raiz e guarda em memória
        if (resource != null && !resource.getContents().isEmpty()) {
            Object root = resource.getContents().get(0);
            if (root instanceof Namespace) {
                this.rootNamespace = (Namespace) root;
            } else {
                throw new RuntimeException("Root element não é um Namespace");
            }
        } else {
            throw new RuntimeException("Falha ao carregar o recurso ou conteúdo vazio: " + fullPath);
        }
    }
    
    
    public void parseFile(String fileName) {

        if (baseFilePath == null || baseFilePath.isEmpty()) {
            throw new RuntimeException("Erro: A variável de ambiente BASE_FILE_PATH não foi definida.");
        }

        String fullPath = baseFilePath + "/" + fileName;
        
        try {
            String fileContent = Files.readString(Path.of(fullPath), Charset.forName("UTF-8"));
            SysMLInteractiveResult result = sysml.process(fileContent);
            System.out.println("Resultado do parser:");
            System.out.println(result.toString());
            if (result.hasErrors()) {
                throw new RuntimeException("Erro ao processar o arquivo SysML: " + result.getException());
            }

            Element root = result.getRootElement();
            if (root instanceof Namespace) {
                this.rootNamespace = (Namespace) root;
                //EcoreUtil.resolveAll(this.rootNamespace);
                //ElementUtil.transformAll(this.rootNamespace.eResource().getResourceSet(), true);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo SysML: " + e.getMessage(), e);
        }
    }


    public void parseFromEnvFile() {
        String fileName = System.getenv("FILE_NAME");

        if (baseFilePath == null || baseFilePath.isEmpty()) {
            throw new RuntimeException("Erro: A variável de ambiente BASE_FILE_PATH não foi definida.");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("Erro: A variável de ambiente FILE_NAME não foi definida.");
        }

        String fullPath = baseFilePath + "/" + fileName;

        try {
            String fileContent = Files.readString(Path.of(fullPath), Charset.forName("UTF-8"));
            SysMLInteractiveResult result = sysml.process(fileContent);

            if (result.hasErrors()) {
                throw new RuntimeException("Erro ao processar o arquivo SysML: " + result.getException());
            }

            Element root = result.getRootElement();
            if (root instanceof Namespace) {
                this.rootNamespace = (Namespace) root;

                // Resolve referências e gera relações implícitas - método novo:
                EcoreUtil.resolveAll(this.rootNamespace);
                ElementUtil.transformAll(this.rootNamespace.eResource().getResourceSet(), true);
                
                
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo SysML: " + e.getMessage(), e);
        }
    }

    public Namespace getRootNamespace() {
        return rootNamespace;
    }
}
