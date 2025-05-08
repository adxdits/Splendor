package fr.uge.splendor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class CardStack implements Stack {
    int totalAmount;
    int cardLevel;
    java.util.Stack<Card> cards;

    public void TokenStack(int cardLevel){
        if (cardLevel < 1 || cardLevel > 3) {
            throw new IllegalArgumentException("Invalid level: " + cardLevel);
        }
        totalAmount = getAmountByLevel(cardLevel);
        initializeCards();
    }


    private void initializeCards() {
        cards = new java.util.Stack<>();
        for (GameColor color: GameColor.values()) {
            if (color == GameColor.YELLOW) {
                continue; // Skip yellow color
            }
            int nbTypes = GameColor.values().length - 1; // Exclude yellow
            for (int i = 0; i < totalAmount/nbTypes; i++) {
                int points = i%4; // no info on points
                cards.add(new Card(cardLevel, color, points));
            }
        }
        Collections.shuffle(cards);
    }

    private int getAmountByLevel(int cardLevel) {
        switch (cardLevel) {
            case 1 -> {
                return 40;
            }
            case 2 -> {
                return 30;
            }
            case 3 -> {
                return 20;
            }
            default -> {
                throw new IllegalArgumentException("Invalid level: " + cardLevel);
            }
        }
    }


    @Override
    public Item takeOne() {
        return cards.pop(); // if empty no yet implemented ?
    }
}
