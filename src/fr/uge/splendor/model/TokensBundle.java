package fr.uge.splendor.model;

import java.util.*;

public class TokensBundle {
    private final Map<GameColor,Integer> bundle;

    public TokensBundle() {
        this.bundle = new TreeMap<>();
        Arrays.stream(GameColor.values()).toList().forEach(color -> bundle.put(color, 0));
    }

    public TokensBundle(Map<GameColor, Integer> bundle) {
        Objects.requireNonNull(bundle);
        Map<GameColor, Integer> copy = new TreeMap<>(bundle);
        if (copy.values().stream().anyMatch(value -> value < 0)) {
            throw new IllegalArgumentException("Token counts must be non-negative");
        }
        Arrays.stream(GameColor.values()).toList().forEach(color -> copy.putIfAbsent(color, 0));
        this.bundle = copy;
    }

    public TokensBundle addToken(GameColor color, int count) {
        Objects.requireNonNull(color);
        if (count < 0) {
            throw new IllegalArgumentException("Color cannot be null and count must be non-negative");
        }
        int currentCount = this.bundle.getOrDefault(color, 0);
        if (currentCount + count < 0) {
            throw new IllegalArgumentException("Token counts must be non-negative");
        }
        this.bundle.put(color, currentCount + count);
        return this;
    }

    public TokensBundle addTokens(TokensBundle other) {
        Objects.requireNonNull(other);
        for (GameColor color : TokensBundle.getColorsSupported()){
            this.addToken(color, other.getTokenCount(color));
        }
        return this;
    }

    public int getTokenCount(GameColor color) {
        return bundle.getOrDefault(color, 0);
    }

    // Check if this TokensBundle can cover the other TokensBundle (with the use of yellow tokens)
    public boolean covers(TokensBundle other) {
        Objects.requireNonNull(other);

        int countExceeded = 0;
        for (GameColor color : GameColor.values()) {
            if(color == GameColor.YELLOW){
                continue;
            }
            if (this.getTokenCount(color) < other.getTokenCount(color)) {
                countExceeded += other.getTokenCount(color) - this.getTokenCount(color);
            }
        }
        return countExceeded <= this.getTokenCount(GameColor.YELLOW) ;
    }

    public TokensBundle subtract(TokensBundle other) {
        Objects.requireNonNull(other);

        for (GameColor color : GameColor.values()){
            subtract(color, other.getTokenCount(color));
        }
        return this;
    }

    public TokensBundle subtract(GameColor color, int count) {
        Objects.requireNonNull(color);
        if (count < 0) {
            throw new IllegalArgumentException("Count must be non-negative");
        }
        int remains = this.getTokenCount(color) - count;

        if (remains >= 0){
            this.bundle.put(color, this.getTokenCount(color) - count);
            return this;
        }

        remains = Math.absExact(remains);
        if (remains > getTokenCount(GameColor.YELLOW)) {
            throw new IllegalArgumentException("Cannot subtract more tokens than available");
        }
        if (remains < 0) {
            this.bundle.put(color, this.getTokenCount(GameColor.YELLOW) - remains);
        }
        return this;
    }

    // return a TokensBundle that contains the positive difference between this and other
    public TokensBundle diffPos(TokensBundle other) {
        Objects.requireNonNull(other);
        System.out.println("diffing " + this + " and " + other);

        TokensBundle result = new TokensBundle();
        for (GameColor color : TokensBundle.getColorsSupported()) {
            int count = Math.max(0,this.getTokenCount(color) - other.getTokenCount(color));
            result.addToken(color, count);
        }
        System.out.println("resulting diff = " + result);
        return result;
    }

    public int getTotalTokens() {
        return bundle.values().stream().mapToInt(Integer::intValue).sum();
    }

    public TokensBundle copy() {
        return new TokensBundle(bundle);
    }

    public static List<GameColor> getColorsSupported(){
        return List.of(GameColor.values());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (GameColor color : GameColor.values()) {
            int count = bundle.get(color);
            if (count > 0) {
                sb.append(color).append(":").append(count).append(" ");
            }
        }
        return sb.toString();
    }


}
