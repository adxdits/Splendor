package fr.uge.splendor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenStack implements Stack {
    private final List<Token> tokens = new ArrayList<>();

    public TokenStack() {
        initializeTokens();
    }

    private void initializeTokens() {
        for (GameColor color : GameColor.values()) {
            if (color != GameColor.YELLOW) {
                for (int i = 0; i < 7; i++) {
                    tokens.add(new Token(color));
                }
            }
        }
        Collections.shuffle(tokens);
    }

    @Override
    public Item takeOne() {
        if (tokens.isEmpty()) {
            throw new IllegalStateException("No tokens left ");
        }
        return tokens.remove(tokens.size() - 1);
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    public int remainingTokens() {
        return tokens.size();
    }
}