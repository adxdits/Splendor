package fr.uge.splendor.model;

import java.util.Objects;

public record Token(GameColor color) {
    public Token{
        Objects.requireNonNull(color);
    }
}
