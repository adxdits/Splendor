package fr.uge.splendor;

public enum GameColor {
    GREEN,
    WHITE,
    BLACK,
    YELLOW, //OR
    RED,
    BLUE;

    static GameColor getGameColorFromString(String color) {
        return switch (color) {
            case "green" -> GREEN;
            case "white" -> WHITE;
            case "black" -> BLACK;
            case "yellow" -> YELLOW;
            case "red" -> RED;
            case "blue" -> BLUE;
            default -> null;
        };
    }
}
