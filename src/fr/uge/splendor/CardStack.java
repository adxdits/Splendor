package fr.uge.splendor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CardStack implements Stack {
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
        cards.addAll(all_cards.get(level));
        Collections.shuffle(cards);
    }


    private void initializeCards() { // simple version
        /*for (GameColor color : GameColor.values()) {
            if (color == GameColor.YELLOW) continue;

            Map<GameColor, Integer> cost = Collections.singletonMap(color, 3);

            for (int i = 0; i < 8; i++) {
                cards.add(new Card(1, color, 1, cost));
            }
        }*/

        Collections.shuffle(cards);
    }

    @Override
    public Item takeOne() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No cards left in the stack");
        }
        return cards.pop();
        //return cards.remove(cards.size() - 1);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

}