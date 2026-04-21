package sysml;

import org.omg.sysml.lang.sysml.Feature;
import java.util.List;

// OUTDATED
public class FeaturePrinter {

    // Método genérico para imprimir as features de um elemento
    public static void printFeatures(Object element, String indent) {
        if (element == null) {
            System.out.println(indent + "Element is null.");
            return;
        }

        // Tenta acessar as features via getOwnedFeature()
        try {
            // Usa reflexão para acessar o método getOwnedFeature()
            var method = element.getClass().getMethod("getOwnedFeature");
            @SuppressWarnings("unchecked")
            List<Feature> features = (List<Feature>) method.invoke(element);

            if (features == null || features.isEmpty()) {
                System.out.println(indent + "No features found.");
                return;
            }

            // Itera pelas features e imprime detalhes
            for (Feature feature : features) {
                String featureName = feature.getName() != null ? feature.getName() : "UnnamedFeature";
                System.out.println(indent + "    Feature: " + featureName + " (Class: " + feature.getClass().getSimpleName() + ")");
            }
        } catch (NoSuchMethodException e) {
            System.out.println(indent + "The element does not have a 'getOwnedFeature()' method.");
        } catch (Exception e) {
            System.out.println(indent + "Error while accessing features: " + e.getMessage());
        }
    }
}
