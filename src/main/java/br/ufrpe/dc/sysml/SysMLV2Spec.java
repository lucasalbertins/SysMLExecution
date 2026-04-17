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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	baseFilePath = properties.getProperty("app.baseFilePath");
        String systemLibPath = properties.getProperty("app.systemLibPath");
        
        if (systemLibPath == null || systemLibPath.isEmpty()) {
            throw new RuntimeException("Error: The environment variable SYSTEM_LIB_PATH has not been defined.");
        }
        sysml = SysMLInteractive.getInstance();
        sysml.setVerbose(false);
        sysml.loadLibrary(systemLibPath);
        sysml.setApiBasePath("http://sysml2.intercax.com:9000");
    }
    
    public void parseFileWithTransform(String fileName) {
        // Builds the full path.
        String fullPath = baseFilePath + "/" + fileName;

        KerMLStandaloneSetup.doSetup();
        SysMLStandaloneSetup.doSetup();
        
        SysMLPackage.eINSTANCE.eClass();

        // Creates a ResourceSet and loads the resource.
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.getResource(URI.createFileURI(fullPath), true);

        EcoreUtil.resolveAll(resourceSet);
        ElementUtil.transformAll(resourceSet, true);

        // Extracts the root namespace and stores it in memory.
        if (resource != null && !resource.getContents().isEmpty()) {
            Object root = resource.getContents().get(0);
            if (root instanceof Namespace) {
                this.rootNamespace = (Namespace) root;
            } else {
                throw new RuntimeException("Root element is not a Namespace.");
            }
        } else {
            throw new RuntimeException("Failed to load resource or empty content: " + fullPath);
        }
    }
    
    public void parseFile(String fileName) {

        if (baseFilePath == null || baseFilePath.isEmpty()) {
            throw new RuntimeException("Error: The environment variable BASE_FILE_PATH has not been defined.");
        }
        String fullPath = baseFilePath + "/" + fileName;
        
        try {
            String fileContent = Files.readString(Path.of(fullPath), Charset.forName("UTF-8"));
            SysMLInteractiveResult result = sysml.process(fileContent);
            System.out.println("Parser result:");
            System.out.println(result.toString());
            
            if (result.hasErrors()) {
                throw new RuntimeException("Error processing SysML file: " + result.getException());
            }
            Element root = result.getRootElement();
            
            if (root instanceof Namespace) {
                this.rootNamespace = (Namespace) root;
                //EcoreUtil.resolveAll(this.rootNamespace);
                //ElementUtil.transformAll(this.rootNamespace.eResource().getResourceSet(), true);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading SysML file: " + e.getMessage(), e);
        }
    }

    public void parseFromEnvFile() {
        String fileName = System.getenv("FILE_NAME");

        if (baseFilePath == null || baseFilePath.isEmpty()) {
            throw new RuntimeException("Error: The environment variable BASE_FILE_PATH has not been defined.");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("Erro: The environment variable FILE_NAME has not been defined.");
        }

        String fullPath = baseFilePath + "/" + fileName;

        try {
            String fileContent = Files.readString(Path.of(fullPath), Charset.forName("UTF-8"));
            SysMLInteractiveResult result = sysml.process(fileContent);

            if (result.hasErrors()) {
                throw new RuntimeException("Error processing SysML file: " + result.getException());
            }
            Element root = result.getRootElement();
            if (root instanceof Namespace) {
                this.rootNamespace = (Namespace) root;
                
                // Resolve referências e gera relações implícitas - método novo:
                EcoreUtil.resolveAll(this.rootNamespace);
                ElementUtil.transformAll(this.rootNamespace.eResource().getResourceSet(), true);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading SysML file: " + e.getMessage(), e);
        }
    }

    public Namespace getRootNamespace() {
        return rootNamespace;
    }
}
