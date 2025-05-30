package fr.uge.splendor.model;

import java.util.Arrays;
import java.util.Objects;

public record Noble(
        int prestigePoints,
        TokensBundle cost
) {

    public Noble {
        if (prestigePoints < 0) {
            throw new IllegalArgumentException("Prestige points cannot be negative");
        }

        Objects.requireNonNull(cost);
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

        return "{ PT:" + prestigePoints + " | Cout:" + costString + " }";
    }
}