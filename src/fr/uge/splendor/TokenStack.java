package fr.uge.splendor;

import java.util.Objects;

public class TokenStack implements Stack {
    int currentAmount;
    int totalAmount;
    GameColor color;

    public TokenStack(GameColor color){
        Objects.requireNonNull(color);
        int amount = getAmountByColor(color);
        totalAmount = amount;
        currentAmount = amount;
    }

    private int getAmountByColor(GameColor color) {
        switch (color) {
            case GameColor.RED -> {
                return 7;
            }
            case GameColor.GREEN -> {
                return 7;
            }
            case GameColor.WHITE -> {
                return 7;
            }
            case GameColor.BLACK -> {
                return 7;
            }
            case GameColor.BLUE -> {
                return 7;
            }
            case GameColor.YELLOW -> {
                return 5;
            }
            default -> {
                throw new IllegalArgumentException("Invalid color: " + color);
            }
        }
    }


    @Override
    public Item takeOne() {
        currentAmount--;
        return new Token(color);
    }
}
