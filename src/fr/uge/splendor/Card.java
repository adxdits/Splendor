package fr.uge.splendor;

import java.util.Objects;

public record Card (int level, GameColor color,int points) implements Item {

    public Card{
        Objects.requireNonNull(color);
        if (level < 1 || level > 3) {
            throw new IllegalArgumentException("Level must be between 1 and 3");
        }
        if (points < 0) {
            throw new IllegalArgumentException("Points must be non-negative");
        }
    }

}