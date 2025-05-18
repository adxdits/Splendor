package fr.uge.splendor;

import java.util.*;

public class Player {
    private final EnumMap<GameColor, Integer> tokens = new EnumMap<>(GameColor.class);
    private final EnumMap<GameColor, Integer> advantages = new EnumMap<>(GameColor.class);
    private final ArrayList<Card> borrowedCards = new ArrayList<>();

    private final List<Card> cards = new ArrayList<>();
    private int prestigePoints;

    public Player() {
        Arrays.stream(GameColor.values())
                .forEach(color -> tokens.put(color, 0));
    }



    public void addTokens(GameColor color, int quantity) {
        tokens.merge(color, quantity, Integer::sum);
    }

    private void removeTokens(Map<GameColor,Integer> tokensToRemove){
        int goldTokenUsed = 0;
        for (Map.Entry<GameColor, Integer> entry : tokensToRemove.entrySet()) {
            GameColor color = entry.getKey();
            int cost = entry.getValue();
            int currentTokens = tokens.getOrDefault(color, 0);
            int bonus = advantages.getOrDefault(color, 0);

            if (cost > currentTokens + bonus){
                goldTokenUsed += cost - currentTokens - bonus;
                tokens.put(color, 0);
            }
            else {
                tokens.put(color, currentTokens + bonus - cost);
            }

        }
        int gold = tokens.getOrDefault(GameColor.YELLOW, 0);
        if (gold < goldTokenUsed) {
            throw new IllegalStateException("Not enough gold tokens to buy the card");
        }
        tokens.put(GameColor.YELLOW, gold - goldTokenUsed);
    }

    public boolean canBorrowCard(){
        return borrowedCards.size() < 3;
    }

    public void borrowCard(Card card) {
        if (!canBorrowCard()) {
            throw new IllegalStateException("Cannot borrow more than 3 cards");
        }
        borrowedCards.add(card);
    }

    public void buyCard(Card card) {
        removeTokens(card.cost());
        prestigePoints += card.prestigePoints();
        cards.add(card);
        advantages.merge(card.color(), 1, Integer::sum);
    }

    public boolean canBuy(Card card) {
        int diff = 0;
        for (Map.Entry<GameColor, Integer> entry : card.cost().entrySet()) {
            GameColor color = entry.getKey();
            int cost = entry.getValue();
            int currentTokens = tokens.getOrDefault(color, 0);
            int bonus = advantages.getOrDefault(color, 0);
            if (cost > currentTokens + bonus) {
                diff += cost - currentTokens - bonus;
            }
        }
        return tokens.getOrDefault(GameColor.YELLOW, 0) >= diff;
    }



    public int getPrestigePoints() {
        return prestigePoints;
    }
}