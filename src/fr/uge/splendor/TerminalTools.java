package fr.uge.splendor;

public class TerminalTools {
    //return a string with the color of interactive text (cyan)
    public static String interactiveText(String text) {
        return "\u001B[36m" + text + "\u001B[0m";
    }
    // return a string with the color of ask to user text (magenta)
    public static String askText(String text) {
        return "\u001B[35m" + text + "\u001B[0m";
    }
    //return a string with the color of warning text (orange)
    public static String warningText(String text) {
        return "\u001B[33m" + text + "\u001B[0m";
    }

    //return a string with the color of confirm text (green)
    public static String confirmText(String text) {
        return "\u001B[32m" + text + "\u001B[0m";
    }

    //return a string with the color of error text (red)
    public static String errorText(String text) {   // in theory, never used
        return "\u001B[31m" + text + "\u001B[0m";
    }
}
