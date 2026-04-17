package br.ufrpe.dc.sysml;

import java.util.ArrayList;
import java.util.List;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.AttributeUsage;
import org.omg.sysml.lang.sysml.StateUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sysml")
public class SysMLParserController {

    @Autowired
    private SysMLV2Spec sysmlV2Spec;

    @GetMapping("/parse")
    public ResponseEntity<String> parseModel(@RequestParam("fileName") String fileName) {
        sysmlV2Spec.parseFile(fileName);
        Namespace root = sysmlV2Spec.getRootNamespace();

        // Collect all StateUsage's from the model.
        List<String> stateUsages = new ArrayList<>();
        collectStateUsages(root, stateUsages);

        // Collect all AttributeUsage's from the model.
        List<String> attributeUsages = new ArrayList<>();
        collectAttributeUsages(root, attributeUsages);

        // Create a response string.
        String response = "StateUsage's found:\n" + String.join("\n", stateUsages) +
                "\n\nAttributeUsage's found:\n" + String.join("\n", attributeUsages);
        return ResponseEntity.ok(response);
    }

    private void collectStateUsages(Element element, List<String> stateUsages) {
        if (element instanceof StateUsage) {
            StateUsage state = (StateUsage) element;
            stateUsages.add(state.getName()); // Adds the state's name.
        }
        // Recursively traverse child elements.
        if (element instanceof Namespace) {
            for (Element child : ((Namespace) element).getOwnedMember()) {
                collectStateUsages(child, stateUsages);
            }
        }
    }

    private void collectAttributeUsages(Element element, List<String> attributeUsages) {
        if (element instanceof AttributeUsage) {
            AttributeUsage attribute = (AttributeUsage) element;
            attributeUsages.add(attribute.getName()); // Adds the attribute's name.
        }
        // Recursively traverse child elements.
        if (element instanceof Namespace) {
            for (Element child : ((Namespace) element).getOwnedMember()) {
                collectAttributeUsages(child, attributeUsages);
            }
        }
    }
}
