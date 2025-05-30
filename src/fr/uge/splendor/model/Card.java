package fr.uge.splendor.model;

import java.util.Arrays;
import java.util.Objects;

public record Card(
        int level,
        GameColor color,
        int prestigePoints,
        TokensBundle cost
) {
    public static final int MAX_LEVEL = 3;
    public Card {
        if (level < 1 || level > MAX_LEVEL) {
            throw new IllegalArgumentException("Level must be between 1 and 3");
        }
        if (prestigePoints < 0) {
            throw new IllegalArgumentException("Prestige points cannot be negative");
        }
        Objects.requireNonNull(cost);
        Objects.requireNonNull(color);

    }

    @Override
    public String toString() {
        StringBuilder costString = new StringBuilder("(");
        Arrays.stream(GameColor.values()).forEach(color -> {
            int count = cost.getTokenCount(color);
            if (count > 0) {
                costString.append(color).append(":").append(count).append(" ");
            }
        });
        costString.append(")");
        return "{ Couleur:" + color + " | Points :" + prestigePoints + " | Cout :" + costString + " }";
    }
}
