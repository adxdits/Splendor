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


    public String toString() {
        return switch (this) {
            case GREEN -> "\uD83D\uDFE9";
            case WHITE -> "\uD83D\uDD33";
            case BLACK -> "\uD83D\uDD32";
            case YELLOW -> "\uD83D\uDFE8";
            case RED -> "\uD83D\uDFE5";
            case BLUE -> "\uD83D\uDFE6";
        };
    }
}
