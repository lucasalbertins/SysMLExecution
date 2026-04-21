package sysml;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import org.omg.sysml.interactive.SysMLInteractive;
import org.omg.sysml.interactive.SysMLInteractiveResult;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;

// OUTDATED
public class SysMLReader {

    public static void main(String[] args) {
        String systemLibPath = System.getenv("SYSTEM_LIB_PATH");
        String filePath = System.getenv("FILE_PATH");

        if (systemLibPath == null || filePath == null) {
            System.out.println("Erro: As variáveis de ambiente não foram definidas.");
            return;
        }

        System.out.println("Loading libraries from: " + systemLibPath);
        System.out.println("Reading file from: " + filePath);

        SysMLInteractive sysml = SysMLInteractive.getInstance();
        sysml.setVerbose(false);
        sysml.loadLibrary(systemLibPath);
        sysml.setApiBasePath("http://sysml2.intercax.com:9000");

        try {
            String fileContent = Files.readString(Path.of(filePath), Charset.forName("UTF-8"));
            SysMLInteractiveResult result = sysml.process(fileContent);

            System.out.println(result.toString());
            if (!result.hasErrors()) {
                Element root = result.getRootElement();
                if (root instanceof Namespace) {
                    Namespace rootNamespace = (Namespace) root;
                    System.out.println("Top-level Namespace: " + rootNamespace.getDeclaredName());

                    // Chama o método de impressão da estrutura
                    ElementPrinter.printElementStructure(rootNamespace, "  ");
                }
            }
            System.out.println("Finished!");

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
