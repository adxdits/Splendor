package fr.uge.splendor;

import java.util.*;

public class Player {
    private final EnumMap<GameColor, Integer> tokens = new EnumMap<>(GameColor.class);
    private final List<Card> cards = new ArrayList<>();
    private int prestigePoints;

    public Player() {
        Arrays.stream(GameColor.values())
                .forEach(color -> tokens.put(color, 0));
    }

    public void addTokens(GameColor color, int quantity) {
        tokens.merge(color, quantity, Integer::sum);
    }

    public void addCard(Card card) {
        cards.add(card);
        prestigePoints += card.prestigePoints();
        card.cost().forEach((c, cost) -> tokens.merge(c, -cost, Integer::sum));
    }

    public boolean canBuy(Card card) {
        return card.cost().entrySet().stream()
                .allMatch(entry -> tokens.get(entry.getKey()) >= entry.getValue());
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }
}