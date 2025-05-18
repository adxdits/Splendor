package fr.uge.splendor;

import java.util.Map;
import java.util.Objects;

public record Card(
        int level,
        GameColor color,
        int prestigePoints,
        Map<GameColor, Integer> cost
) implements Item {

    public Card {
        if (level < 1 || level > 3) {
            throw new IllegalArgumentException("Level must be between 1 and 3");
        }
        if (prestigePoints < 0) {
            throw new IllegalArgumentException("Prestige points cannot be negative");
        }
        Objects.requireNonNull(cost);
        Objects.requireNonNull(color);

    }
}
