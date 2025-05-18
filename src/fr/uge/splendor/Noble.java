package fr.uge.splendor;

import java.util.Map;
import java.util.Objects;

public record Noble(
        int prestigePoints,
        Map<GameColor, Integer> cost
) implements Item {

    public Noble {
        if (prestigePoints < 0) {
            throw new IllegalArgumentException("Prestige points cannot be negative");
        }
        Objects.requireNonNull(cost);
    }

    @Override
    public String toString() {
        String costString = cost.entrySet().stream()
                .map(entry -> entry.getKey().shortName() + ":" + entry.getValue())
                .reduce("(", (a, b) -> a + " " + b) + " )";
        return "{ PV:" + prestigePoints + " | Cost:" + costString + " }";
    }
}