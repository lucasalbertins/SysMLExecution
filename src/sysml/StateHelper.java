package sysml;

import org.omg.sysml.lang.sysml.*;

// OUTDATED
public class StateHelper {
    public static void printState(StateUsage state, String indent) {
        System.out.println(indent + "StateUsage: " + state.getDeclaredName());

        printEntryAction(state, indent + "  ");
        printDoAction(state, indent + "  ");
        printExitAction(state, indent + "  ");

        // Imprime sub-estados
        for (StateUsage subState : state.getNestedState()) {
            printState(subState, indent + "  ");
        }
    }

    private static void printEntryAction(StateUsage state, String indent) {
        ActionUsage entryAction = state.getEntryAction();
        if (entryAction != null) {
            System.out.println(indent + "Entry Action: " + entryAction.getName());
        }
    }

    private static void printDoAction(StateUsage state, String indent) {
        ActionUsage doAction = state.getDoAction();
        if (doAction != null) {
            System.out.println(indent + "Do Action: " + doAction.getName());
            for (Feature input : doAction.inputParameters()) {
                System.out.println(indent + "  Input Parameter: " + input.getName());
            }

            // Explora AssignmentActionUsage, se necessário
            if (doAction instanceof AssignmentActionUsage) {
                AssignmentHelper.printAssignment((AssignmentActionUsage) doAction, indent + "    ");
            }
        }
    }

    private static void printExitAction(StateUsage state, String indent) {
        ActionUsage exitAction = state.getExitAction();
        if (exitAction != null) {
            System.out.println(indent + "Exit Action: " + exitAction.getName());
        }
    }
}