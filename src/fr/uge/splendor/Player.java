package fr.uge.splendor;

import java.util.*;

public class Player {
    private final EnumMap<GameColor, Integer> tokens = new EnumMap<>(GameColor.class);
    private final EnumMap<GameColor, Integer> advantages = new EnumMap<>(GameColor.class);
    private final ArrayList<Card> borrowedCards = new ArrayList<>();
    private final List<Card> cards = new ArrayList<>();
    private final List<Noble> nobles = new ArrayList<>();
    private int prestigePoints;
    private final String name;

    public Player(int playerNumber) {
        Arrays.stream(GameColor.values())
                .forEach(color -> tokens.put(color, 0));
        name = "Player " + playerNumber;
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

    public boolean canBuyCard(Card card) {
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

    public boolean canGetNoble(Noble noble){
        for (Map.Entry<GameColor, Integer> entry : noble.cost().entrySet()) {
            GameColor color = entry.getKey();
            int cost = entry.getValue();
            int currentColorAdvantage = advantages.getOrDefault(color, 0);
            if (cost > currentColorAdvantage) {
                return false;
            }
        }
        return true;
    }

    public void takeNoble(Noble noble){
        nobles.add(noble);
        prestigePoints += noble.prestigePoints();
    }


    public int getPrestigePoints() {
        return prestigePoints;
    }

    public String getName() {
        return name;
    }

    public void showState() {
        showPrestigePoints();
        showAdvantages();
        showTokens();

    }

    private void showTokens() {
        StringBuilder sb = new StringBuilder("Jetons :{ ");
        for (Map.Entry<GameColor, Integer> entry : tokens.entrySet()) {
            GameColor color = entry.getKey();
            int quantity = entry.getValue();
            sb.append(color.shortName()).append(":").append(quantity).append(" ");
        }
        sb.append("}");
        System.out.println(sb);
    }

    private void showAdvantages() {
        StringBuilder sb = new StringBuilder("Avantages :{ ");
        for (Map.Entry<GameColor, Integer> entry : advantages.entrySet()) {
            GameColor color = entry.getKey();
            int quantity = entry.getValue();
            if (color == GameColor.YELLOW) continue;
            sb.append(color.shortName()).append(":").append(quantity).append(" ");
        }
        sb.append("}");
    }

    private void showPrestigePoints() {
        System.out.println("PV: " + prestigePoints);
    }
}