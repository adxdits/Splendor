package fr.uge.splendor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TokenStack implements Stack {
    private int tokens;
    private final GameColor color;


    public TokenStack(GameColor color, int nbPlayer) {
        Objects.requireNonNull(color);
        tokens = tokensByPlayer(color, nbPlayer);
        this.color = color;
    }

    private int tokensByPlayer(GameColor color, int nbPlayer) {
        int tokens = 0;
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


    @Override
    public Item takeOne() {
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

    @Override
    public String toString() {
        return "{ C:" + color + " | Q:" + tokens + " }";
    }
}