package fr.uge.splendor.model;

import java.util.Map;
import java.util.Objects;

public record Noble(
        int prestigePoints,
        Map<GameColor, Integer> cost
) {

    public Noble {
        if (prestigePoints < 0) {
            throw new IllegalArgumentException("Prestige points cannot be negative");
        }

        Objects.requireNonNull(cost);
    }

    @Override
    public String toString() {
        String costString = cost.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .reduce("(", (a, b) -> a + " " + b) + " )";
        return "{ PT:" + prestigePoints + " | Cout:" + costString + " }";
    }
}