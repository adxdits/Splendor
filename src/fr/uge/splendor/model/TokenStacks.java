package fr.uge.splendor.model;

import java.util.Objects;

public class TokenStacks {
    private final TokensBundle stacks;
    private final TokensBundle maximumTokens = new TokensBundle();


    public TokenStacks(int nbPlayer) {
        Objects.checkIndex(nbPlayer, 4);
        TokensBundle.getColorsSupported().forEach(color -> maximumTokens.addToken(color, tokensByPlayer(color, nbPlayer)));
        stacks = maximumTokens.copy();

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
        return 1;//tokens;
    }

    public void refill(GameColor color, int nbToken){
        if (nbToken < 0) {
            throw new IllegalArgumentException("Number of tokens to refill cannot be negative");
        }
        if (stacks.getTokenCount(color) + nbToken > maximumTokens.getTokenCount(color)) {
            throw new IllegalStateException("Cannot exceed maximum tokens in the stack");
        }
        stacks.addToken(color, nbToken);
    }

    public GameColor takeOne(GameColor color) {
        if (stacks.getTokenCount(color) <= 0) {
            throw new IllegalStateException("No tokens left in the stack");
        }
        stacks.subtract(color, 1);

        return color;
    }

    public boolean isEmpty(GameColor color) {
        return stacks.getTokenCount(color) == 0;
    }

    public int remainingTokens(GameColor color) {
        return stacks.getTokenCount(color);
    }

    public TokensBundle getStacksState() {
        return stacks.copy();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (GameColor color : TokensBundle.getColorsSupported()){
            int count = stacks.getTokenCount(color);
            sb.append(color).append(":").append(count).append(" ");
        }
        return sb.toString();
    }
}