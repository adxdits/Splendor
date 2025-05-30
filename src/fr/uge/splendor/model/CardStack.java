package fr.uge.splendor.model;

import fr.uge.splendor.tools.TerminalTools;
import fr.uge.splendor.tools.Tools;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CardStack {
    private static Map<Integer,List<Card>> all_cards = new HashMap<>(); // level -> cards
    private final java.util.Stack<Card> cards = new java.util.Stack<>();

    public static void loadCardFromCSV() {
        try {
            Path path = Path.of("cards_list.csv");
            all_cards = Tools.LoadingCardsOnCSV(path).stream().collect(Collectors.groupingBy(Card::level,Collectors.toList()));
        } catch (IOException e) {
            System.err.println(TerminalTools.errorText("Error loading cards: " + e.getMessage()));
            throw new RuntimeException("Failed to load cards from CSV", e);
        }
    }

    public CardStack(int level) {
        initializeCards(level,false);
    }

    public CardStack(boolean useSimpleGame){
        initializeCards(1,useSimpleGame);
    }

    private void initializeCards(int level,boolean useSimpleGame) {
        if (useSimpleGame) {
            for (GameColor color : TokensBundle.getColorsSupported()) {
                if (color == GameColor.YELLOW) continue;
                TokensBundle cost = new TokensBundle(Map.of(color, 3));
                for (int i = 0; i < 8; i++) {
                    cards.add(new Card(level, color, 3, cost));
                }
            }
        }else {
            cards.addAll(all_cards.get(level));
        }

        Collections.shuffle(cards);
    }

    public Card takeOne() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No cards left in the stack");
        }
        return cards.pop();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

}