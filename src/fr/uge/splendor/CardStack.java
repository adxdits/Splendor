package fr.uge.splendor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CardStack implements Stack {
    private final List<Card> cards = new ArrayList<>();

    public CardStack() {
        initializeCards();
    }

    private void initializeCards() {
        for (GameColor color : GameColor.values()) {
            if (color == GameColor.YELLOW) continue;

            Map<GameColor, Integer> cost = Collections.singletonMap(color, 3);

            for (int i = 0; i < 8; i++) {
                cards.add(new Card(1, color, 1, cost));
            }
        }
        Collections.shuffle(cards);
    }

    @Override
    public Item takeOne() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No cards left in the stack");
        }
        return cards.remove(cards.size() - 1);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}