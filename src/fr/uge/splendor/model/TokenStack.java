package fr.uge.splendor.model;

import java.util.Objects;

public class TokenStack {
    private int tokens;
    private final GameColor color;
    private final int maximumTokens;


    public TokenStack(GameColor color, int nbPlayer) {
        Objects.requireNonNull(color);
        tokens = tokensByPlayer(color, nbPlayer);
        this.color = color;
        this.maximumTokens = tokens;
    }

    private int tokensByPlayer(GameColor color, int nbPlayer) {
        int tokens;
        if (color == GameColor.YELLOW) {
            tokens = 5;
        }else {
            switch (nbPlayer) {
                case 2 -> tokens = 4;
                case 3 -> tokens = 5;
                case 4 -> tokens = 7;
                default -> throw new IllegalArgumentException("Invalid number of players: " + nbPlayer);
            }
        }
        return tokens;
    }

    public void refill(int nbToken){
        if (nbToken < 0) {
            throw new IllegalArgumentException("Number of tokens to refill cannot be negative");
        }
        if (tokens + nbToken > maximumTokens) {
            throw new IllegalStateException("Cannot exceed maximum tokens in the stack");
        }
        tokens += nbToken;
    }

    public Token takeOne() {
        if (tokens <= 0) {
            throw new IllegalStateException("No tokens left in the stack");
        }
        tokens--;
        return new Token(color);
    }

    public boolean isEmpty() {
        return tokens == 0;
    }

    public int remainingTokens() {
        return tokens;
    }

    public GameColor getColor() {
        return color;
    }

    @Override
    public String toString() {
        return color + " : " + tokens;
    }
}