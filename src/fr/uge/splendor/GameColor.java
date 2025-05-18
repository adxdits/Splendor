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

    public String shortName() {
        return switch (this) {
            case GREEN -> "V";
            case WHITE -> "BC";
            case BLACK -> "N";
            case YELLOW -> "J";
            case RED -> "R";
            case BLUE -> "B";
        };
    }

    public String toString() {
        return switch (this) {
            case GREEN -> "Vert";
            case WHITE -> "Blanc";
            case BLACK -> "Noir";
            case YELLOW -> "Jaune";
            case RED -> "Rouge";
            case BLUE -> "Bleu";
        };
    }
}
