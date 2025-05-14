package fr.uge.splendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.uge.splendor.GameColor;

public class Player {
    private final String name;
    private final Map<GameColor, Integer> tokens;
    private final List<Card> cards;
    private int prestige;
    
    public Player(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        // Use streams to initialize tokens
        this.tokens = Arrays.stream(GameColor.values())
                .collect(Collectors.toMap(color -> color, color -> 0));
        this.cards = new ArrayList<>();
        this.prestige = 0;
    }
    
    public void addToken(GameColor color) {
        Objects.requireNonNull(color, "Color cannot be null");
        tokens.put(color, tokens.get(color) + 1);
    }
    
    public boolean removeToken(GameColor color) {
        Objects.requireNonNull(color, "Color cannot be null");
        if (tokens.get(color) <= 0) {
            return false;
        }
        tokens.put(color, tokens.get(color) - 1);
        return true;
    }
    
    public void addCard(Card card) {
        Objects.requireNonNull(card, "Card cannot be null");
        cards.add(card);
        prestige += card.points();
    }
    
@Override
public String toString() {
    return "Player " + name + " (Prestige: " + prestige + ", Tokens: " + 
           tokens.entrySet().stream()
               .filter(e -> e.getValue() > 0)
               .map(e -> e.getValue() + " " + e.getKey())
               .collect(Collectors.joining(", ", "[", "]")) + ")";
}

}