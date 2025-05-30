package fr.uge.splendor.model;

import fr.uge.splendor.controller.GameSettings;

import java.util.*;

public class Board {

    public static final int CARDS_BY_LEVEL = 4;

    private final TokenStacks tokenStacks;
    private final List<CardStack> cardStack;
    private final NobleStack nobleStack = new NobleStack();
    private final ArrayList<Noble> noblesShow = new ArrayList<>();
    private final List<List<Card>> cardsShow = new ArrayList<>();

    public Board(GameSettings settings){
        if(settings.useSimplePlay()){
            cardStack = List.of(new CardStack(true));
        }else{
            cardStack = List.of(new CardStack(1),new CardStack(2),new CardStack(3));
        }
        tokenStacks = new TokenStacks(settings.playerCount());
        prepareBoard(settings);
    }

    private void prepareBoard(GameSettings settings) {
        prepareCards();
        prepareNobles(settings);
    }

    private void prepareNobles(GameSettings settings) {
        if (settings.useSimplePlay()){
            return;
        }
        int noblesToShow = getNoblesToShow(settings.playerCount());
        for (int i = 0; i < noblesToShow; i++) {
            Noble noble = nobleStack.takeOne();
            noblesShow.add(noble);
        }
    }

    private void prepareCards() {
        for (CardStack stack : cardStack) {
            ArrayList<Card> cards = new ArrayList<>();
            for (int i = 0; i < CARDS_BY_LEVEL; i++) {
                if (!stack.isEmpty()) {
                    Card card = stack.takeOne();
                    cards.add(card);
                }
            }
            cardsShow.add(cards);
        }
    }
    


    private int getNoblesToShow(int playerNumber) {
        return  playerNumber + 1;
    }

    public void refillTokenStack(TokensBundle tokens){
        for (GameColor color: TokensBundle.getColorsSupported()) {
            int quantity = tokens.getTokenCount(color);
            tokenStacks.refill(color,quantity);
        }
    }

    public void refillTokenStack(GameColor color, int quantity) {
        tokenStacks.refill(color, quantity);
    }
    /* CARD FUNCTION */
    public void cardTaken(Card card) {
        int stackIndex = card.level()-1;
        for (int i =0 ; i<cardsShow.get(stackIndex).size(); i++) {
            Card c = cardsShow.get(stackIndex).get(i);
            if (c.equals(card)) {
                var newCard = cardStack.get(stackIndex).takeOne();
                cardsShow.get(stackIndex).set(i, newCard);
                return;
            }
        }
    }

    public Card peekCard(int level, int index) {
        if (level < 1 || level > cardStack.size()) {
            throw new IllegalArgumentException("Invalid level: " + level);
        }
        if (index < 0 || index >= Board.CARDS_BY_LEVEL) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        int stackIndex = level - 1;
        return cardsShow.get(stackIndex).get(index);
    }

    public List<Card> peekCardsLevel(int level) {
        if (level < 1 || level > cardStack.size()) {
            throw new IllegalArgumentException("Invalid level: " + level);
        }
        return List.copyOf(cardsShow.get(level - 1));
    }

    public List<List<Card>> peekCards() {
        List<List<Card>> allCards = new ArrayList<>();
        for (int i = 0; i < cardStack.size(); i++) {
            allCards.add(List.copyOf(cardsShow.get(i)));
        }
        return allCards;
    }

    public int getNbOfLevels() {
        return cardStack.size();
    }

    public int getCardCountByLevel(int level) {
        if (level < 1 || level > cardStack.size()) {
            throw new IllegalArgumentException("Invalid level: " + level);
        }
        int index = level - 1;
        return cardsShow.get(index).size();
    }




    /* NOBLE FUNCTION */
    public Noble peekNoble(int index) {
        if (index < 0 || index >= noblesShow.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        return noblesShow.get(index);
    }

    public List<Noble> peekNobles() {
        return List.copyOf(noblesShow);
    }

    public void nobleHasBeenTaken(Noble noble) {
        for (int i = 0; i < noblesShow.size(); i++) {
            if (noblesShow.get(i).equals(noble)) {
                noblesShow.set(i, null);
                return;
            }
        }
    }

    public int getNobleCount() {
        return noblesShow.size();
    }

    public GameColor takeToken(GameColor color) {
        return tokenStacks.takeOne(color);
    }

    public GameColor takeToken(GameColor color, int quantity) {
        for (int i = 0; i < quantity; i++) {
            tokenStacks.takeOne(color);
        }
        return color;
    }

    public int getTokenCount(GameColor color) {
        return tokenStacks.remainingTokens(color);
    }

    public TokensBundle getStacksState() {
         return tokenStacks.getStacksState();
    }
}
