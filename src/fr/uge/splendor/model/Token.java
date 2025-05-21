package fr.uge.splendor.model;

import java.util.Objects;

public record Token(GameColor color) implements Item {
    public Token{
        Objects.requireNonNull(color);
    }
}
