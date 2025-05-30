package fr.uge.splendor.model;

import java.util.*;

public class Player {

    private final TokensBundle tokens = new TokensBundle();
    private final TokensBundle advantages = new TokensBundle();
    private final ArrayList<Card> borrowedCards = new ArrayList<>();
    private final List<Card> cards = new ArrayList<>();
    private final List<Noble> nobles = new ArrayList<>();
    private int prestigePoints;
    private final String name;

    public Player(int playerNumber) {
        name = "Player " + playerNumber;
    }

    public void addTokens(GameColor color, int quantity) {
        tokens.addToken(color, quantity);
    }

    public void addTokens(TokensBundle tokens) {
        this.tokens.addTokens(tokens);
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

    public TokensBundle buyCard(Card card) {
        TokensBundle cost = card.cost().diffPos(advantages);
        TokensBundle before = tokens.copy();
        tokens.subtract(cost);
        TokensBundle subtracted = before.diffPos(tokens);

        prestigePoints += card.prestigePoints();
        cards.add(card);
        advantages.addToken(card.color(), 1);
        borrowedCards.remove(card);
        return subtracted;
    }

    public boolean canBuyCard(Card card) {
        TokensBundle all = tokens.copy().addTokens(advantages);
        return all.covers(card.cost());
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


    public List<Card> getBorrowedCards() {
        return List.copyOf(borrowedCards);
    }

    public int countTokens() {
        return tokens.getTotalTokens();
    }

    public void throwToken(Token token){
        tokens.subtract(token.color(), 1);
    }

    public TokensBundle getTokens() {
        return tokens.copy();
    }

    public TokensBundle getAdvantages() {
        return advantages.copy();
    }
}